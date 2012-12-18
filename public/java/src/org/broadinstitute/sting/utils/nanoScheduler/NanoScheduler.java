package org.broadinstitute.sting.utils.nanoScheduler;

import com.google.java.contract.Ensures;
import com.google.java.contract.Requires;
import org.apache.log4j.Logger;
import org.broadinstitute.sting.utils.MultiThreadedErrorTracker;
import org.broadinstitute.sting.utils.threading.NamedThreadFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * Framework for very fine grained MapReduce parallelism
 *
 * The overall framework works like this
 *
 * nano <- new Nanoschedule(bufferSize, numberOfMapElementsToProcessTogether, nThreads)
 * List[Input] outerData : outerDataLoop )
 *   result = nano.execute(outerData.iterator(), map, reduce)
 *
 * bufferSize determines how many elements from the input stream are read in one go by the
 * nanoscheduler.  The scheduler may hold up to bufferSize in memory at one time, as well
 * as up to bufferSize map results as well.
 *
 * numberOfMapElementsToProcessTogether determines how many input elements are processed
 * together each thread cycle.  For example, if this value is 10, then the input data
 * is grouped together in units of 10 elements each, and map called on each in term.  The more
 * heavy-weight the map function is, in terms of CPU costs, the more it makes sense to
 * have this number be small.  The lighter the CPU cost per element, though, the more this
 * parameter introduces overhead due to need to context switch among threads to process
 * each input element.  A value of -1 lets the nanoscheduler guess at a reasonable trade-off value.
 *
 * nThreads is a bit obvious yes?  Note though that the nanoscheduler assumes that it gets 1 thread
 * from its client during the execute call, as this call blocks until all work is done.  The caller
 * thread is put to work by execute to help with the processing of the data.  So in reality the
 * nanoScheduler only spawn nThreads - 1 additional workers (if this is > 1).
 *
 * User: depristo
 * Date: 8/24/12
 * Time: 9:47 AM
 */
public class NanoScheduler<InputType, MapType, ReduceType> {
    private final static Logger logger = Logger.getLogger(NanoScheduler.class);
    private final static boolean ALLOW_SINGLE_THREAD_FASTPATH = true;
    private final static boolean LOG_MAP_TIMES = false;

    final int bufferSize;
    final int nThreads;
    final ExecutorService masterExecutor;
    final ExecutorService mapExecutor;
    final Semaphore runningMapJobSlots;
    final MultiThreadedErrorTracker errorTracker = new MultiThreadedErrorTracker();

    boolean shutdown = false;
    boolean debug = false;
    private NSProgressFunction<InputType> progressFunction = null;

    /**
     * Create a new nanoscheduler with the desire characteristics requested by the argument
     *
     * @param nThreads the number of threads to use to get work done, in addition to the
     *                 thread calling execute
     */
    public NanoScheduler(final int nThreads) {
        this(nThreads*100, nThreads);
    }

    protected NanoScheduler(final int bufferSize, final int nThreads) {
        if ( bufferSize < 1 ) throw new IllegalArgumentException("bufferSize must be >= 1, got " + bufferSize);
        if ( nThreads < 1 ) throw new IllegalArgumentException("nThreads must be >= 1, got " + nThreads);

        this.bufferSize = bufferSize;
        this.nThreads = nThreads;

        if ( nThreads == 1 ) {
            this.mapExecutor = this.masterExecutor = null;
            runningMapJobSlots = null;
        } else {
            this.masterExecutor = Executors.newSingleThreadExecutor(new NamedThreadFactory("NS-master-thread-%d"));
            this.mapExecutor = Executors.newFixedThreadPool(nThreads, new NamedThreadFactory("NS-map-thread-%d"));
            runningMapJobSlots = new Semaphore(this.bufferSize);
        }
    }

    /**
     * The number of parallel map threads in use with this NanoScheduler
     * @return
     */
    @Ensures("result > 0")
    public int getnThreads() {
        return nThreads;
    }

    /**
     * The input buffer size used by this NanoScheduler
     * @return
     */
    @Ensures("result > 0")
    public int getBufferSize() {
        return this.bufferSize;
    }

    /**
     * Tells this nanoScheduler to shutdown immediately, releasing all its resources.
     *
     * After this call, execute cannot be invoked without throwing an error
     */
    public void shutdown() {
        if ( nThreads > 1 ) {
            shutdownExecutor("mapExecutor", mapExecutor);
            shutdownExecutor("masterExecutor", masterExecutor);
        }

        shutdown = true;
    }

    /**
     * Helper function to cleanly shutdown an execution service, checking that the execution
     * state is clean when it's done.
     *
     * @param name a string name for error messages for the executorService we are shutting down
     * @param executorService the executorService to shut down
     */
    @Requires({"name != null", "executorService != null"})
    @Ensures("executorService.isShutdown()")
    private void shutdownExecutor(final String name, final ExecutorService executorService) {
        if ( executorService.isShutdown() || executorService.isTerminated() )
            throw new IllegalStateException("Executor service " + name + " is already shut down!");

        final List<Runnable> remaining = executorService.shutdownNow();
        if ( ! remaining.isEmpty() )
            throw new IllegalStateException(remaining.size() + " remaining tasks found in an executor " + name + ", unexpected behavior!");
    }

    /**
     * @return true if this nanoScheduler is shutdown, or false if its still open for business
     */
    public boolean isShutdown() {
        return shutdown;
    }

    /**
     * @return are we displaying verbose debugging information about the scheduling?
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Helper function to display a String.formatted message if we are doing verbose debugging
     *
     * @param format the format argument suitable for String.format
     * @param args the arguments for String.format
     */
    @Requires("format != null")
    protected void debugPrint(final String format, Object ... args) {
        if ( isDebug() )
            logger.warn("Thread " + Thread.currentThread().getId() + ":" + String.format(format, args));
    }

    /**
     * Turn on/off verbose debugging
     *
     * @param debug true if we want verbose debugging
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Set the progress callback function to progressFunction
     *
     * The progress callback is invoked after each buffer size elements have been processed by map/reduce
     *
     * @param progressFunction a progress function to call, or null if you don't want any progress callback
     */
    public void setProgressFunction(final NSProgressFunction<InputType> progressFunction) {
        this.progressFunction = progressFunction;
    }

    /**
     * Execute a map/reduce job with this nanoScheduler
     *
     * Data comes from inputReader.  Will be read until hasNext() == false.
     * map is called on each element provided by inputReader.  No order of operations is guarenteed
     * reduce is called in order of the input data provided by inputReader on the result of map() applied
     * to each element.
     *
     * Note that the caller thread is put to work with this function call.  The call doesn't return
     * until all elements have been processes.
     *
     * It is safe to call this function repeatedly on a single nanoScheduler, at least until the
     * shutdown method is called.
     *
     * Note that this function goes through a single threaded fast path if the number of threads
     * is 1.
     *
     * @param inputReader an iterator providing us with the input data to nanoSchedule map/reduce over
     * @param map the map function from input type -> map type, will be applied in parallel to each input
     * @param reduce the reduce function from map type + reduce type -> reduce type to be applied in order to map results
     * @return the last reduce value
     */
    public ReduceType execute(final Iterator<InputType> inputReader,
                              final NSMapFunction<InputType, MapType> map,
                              final ReduceType initialValue,
                              final NSReduceFunction<MapType, ReduceType> reduce) {
        if ( isShutdown() ) throw new IllegalStateException("execute called on already shutdown NanoScheduler");
        if ( inputReader == null ) throw new IllegalArgumentException("inputReader cannot be null");
        if ( map == null ) throw new IllegalArgumentException("map function cannot be null");
        if ( reduce == null ) throw new IllegalArgumentException("reduce function cannot be null");

        ReduceType result;
        if ( ALLOW_SINGLE_THREAD_FASTPATH && getnThreads() == 1 ) {
            result = executeSingleThreaded(inputReader, map, initialValue, reduce);
        } else {
            result = executeMultiThreaded(inputReader, map, initialValue, reduce);
        }

        return result;
    }

    /**
     * Simple efficient reference implementation for single threaded execution.
     *
     * @return the reduce result of this map/reduce job
     */
    @Requires({"inputReader != null", "map != null", "reduce != null"})
    private ReduceType executeSingleThreaded(final Iterator<InputType> inputReader,
                                             final NSMapFunction<InputType, MapType> map,
                                             final ReduceType initialValue,
                                             final NSReduceFunction<MapType, ReduceType> reduce) {
        ReduceType sum = initialValue;
        int i = 0;

        while ( true ) {
            // start timer to ensure that both hasNext and next are caught by the timer
            if ( ! inputReader.hasNext() ) {
                break;
            } else {
                final InputType input = inputReader.next();

                // map
                final MapType mapValue = map.apply(input);

                if ( progressFunction != null )
                    progressFunction.progress(input);

                // reduce
                sum = reduce.apply(mapValue, sum);
            }
        }

        return sum;
    }

    /**
     * Efficient parallel version of Map/Reduce
     *
     * @return the reduce result of this map/reduce job
     */
    @Requires({"inputReader != null", "map != null", "reduce != null"})
    private ReduceType executeMultiThreaded(final Iterator<InputType> inputReader,
                                            final NSMapFunction<InputType, MapType> map,
                                            final ReduceType initialValue,
                                            final NSReduceFunction<MapType, ReduceType> reduce) {
        debugPrint("Executing nanoScheduler");

        // start up the master job
        final MasterJob masterJob = new MasterJob(inputReader, map, initialValue, reduce);
        final Future<ReduceType> reduceResult = masterExecutor.submit(masterJob);

        while ( true ) {
            // check that no errors occurred while we were waiting
            handleErrors();
//            checkForDeadlocks();

            try {
                final ReduceType result = reduceResult.get(100, TimeUnit.MILLISECONDS);

                // in case an error occurred in the reduce
                handleErrors();

                // return our final reduce result
                return result;
            } catch (final TimeoutException ex ) {
                // a normal case -- we just aren't done
            } catch (final InterruptedException ex) {
                errorTracker.notifyOfError(ex);
                // will handle error in the next round of the for loop
            } catch (final ExecutionException ex) {
                errorTracker.notifyOfError(ex);
                // will handle error in the next round of the for loop
            }
        }
    }

//    private void checkForDeadlocks() {
//        if ( deadLockCheckCounter++ % 100 == 0 ) {
//            logger.info("Checking for deadlocks...");
//            final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
//            final long[] threadIds = bean.findDeadlockedThreads(); // Returns null if no threads are deadlocked.
//
//            if (threadIds != null) {
//                final ThreadInfo[] infos = bean.getThreadInfo(threadIds);
//
//                logger.error("!!! Deadlock detected !!!!");
//                for (final ThreadInfo info : infos) {
//                    logger.error("Thread " + info);
//                    for ( final StackTraceElement elt : info.getStackTrace() ) {
//                        logger.error("\t" + elt.toString());
//                    }
//                }
//            }
//        }
//    }

    private void handleErrors() {
        if ( errorTracker.hasAnErrorOccurred() ) {
            masterExecutor.shutdownNow();
            mapExecutor.shutdownNow();
            errorTracker.throwErrorIfPending();
        }
    }

    /**
     * MasterJob has the task to enqueue Map jobs and wait for the final reduce
     *
     * It must be run in a separate thread in order to properly handle errors that may occur
     * in the input, map, or reduce jobs without deadlocking.
     *
     * The result of this callable is the final reduce value for the input / map / reduce jobs
     */
    private class MasterJob implements Callable<ReduceType> {
        final Iterator<InputType> inputReader;
        final NSMapFunction<InputType, MapType> map;
        final ReduceType initialValue;
        final NSReduceFunction<MapType, ReduceType> reduce;

        private MasterJob(Iterator<InputType> inputReader, NSMapFunction<InputType, MapType> map, ReduceType initialValue, NSReduceFunction<MapType, ReduceType> reduce) {
            this.inputReader = inputReader;
            this.map = map;
            this.initialValue = initialValue;
            this.reduce = reduce;
        }

        @Override
        public ReduceType call() {
            // Create the input producer and start it running
            final InputProducer<InputType> inputProducer = new InputProducer<InputType>(inputReader);

            // a priority queue that stores up to bufferSize elements
            // produced by completed map jobs.
            final PriorityBlockingQueue<MapResult<MapType>> mapResultQueue =
                    new PriorityBlockingQueue<MapResult<MapType>>();

            final Reducer<MapType, ReduceType> reducer
                    = new Reducer<MapType, ReduceType>(reduce, errorTracker, initialValue);

            try {
                int nSubmittedJobs = 0;

                while ( continueToSubmitJobs(nSubmittedJobs, inputProducer) ) {
                    // acquire a slot to run a map job.  Blocks if too many jobs are enqueued
                    runningMapJobSlots.acquire();

                    mapExecutor.submit(new ReadMapReduceJob(inputProducer, mapResultQueue, map, reducer));
                    nSubmittedJobs++;
                }

                // mark the last job id we've submitted so we now the id to wait for
                //logger.warn("setting jobs submitted to " + nSubmittedJobs);
                reducer.setTotalJobCount(nSubmittedJobs);

                // wait for all of the input and map threads to finish
                return waitForCompletion(inputProducer, reducer);
            } catch (Throwable ex) {
//                logger.warn("Reduce job got exception " + ex);
                errorTracker.notifyOfError(ex);
                return initialValue;
            }
        }

        /**
         * Wait until the input thread and all map threads have completed running, and return the final reduce result
         */
        private ReduceType waitForCompletion(final InputProducer<InputType> inputProducer,
                                             final Reducer<MapType, ReduceType> reducer) throws InterruptedException {
            // wait until we have a final reduce result
//        logger.warn("waiting for final reduce");
            final ReduceType finalSum = reducer.waitForFinalReduce();

            // wait for all the map threads to finish by acquiring and then releasing all map job semaphores
//        logger.warn("waiting on map");
            runningMapJobSlots.acquire(bufferSize);
            runningMapJobSlots.release(bufferSize);

            // everything is finally shutdown, return the final reduce value
            return finalSum;
        }

        /**
         * Should we continue to submit jobs given the number of jobs already submitted and the
         * number of read items in inputProducer?
         *
         * We continue to submit jobs while inputProducer hasn't reached EOF or the number
         * of jobs we've enqueued isn't the number of read elements.  This means that in
         * some cases we submit more jobs than total read elements (cannot know because of
         * multi-threading) so map jobs must handle the case where getNext() returns EOF.
         *
         * @param nJobsSubmitted
         * @param inputProducer
         * @return
         */
        private boolean continueToSubmitJobs(final int nJobsSubmitted, final InputProducer<InputType> inputProducer) {
            final int nReadItems = inputProducer.getNumInputValues();
            return nReadItems == -1 || nJobsSubmitted < nReadItems;
        }
    }

    private class ReadMapReduceJob implements Runnable {
        final InputProducer<InputType> inputProducer;
        final PriorityBlockingQueue<MapResult<MapType>> mapResultQueue;
        final NSMapFunction<InputType, MapType> map;
        final Reducer<MapType, ReduceType> reducer;

        private ReadMapReduceJob(final InputProducer<InputType> inputProducer,
                                 final PriorityBlockingQueue<MapResult<MapType>> mapResultQueue,
                                 final NSMapFunction<InputType, MapType> map,
                                 final Reducer<MapType, ReduceType> reducer) {
            this.inputProducer = inputProducer;
            this.mapResultQueue = mapResultQueue;
            this.map = map;
            this.reducer = reducer;
        }

        @Override
        public void run() {
            try {
                // get the next item from the input producer
                final InputProducer<InputType>.InputValue inputWrapper = inputProducer.next();

                // depending on inputWrapper, actually do some work or not, putting result input result object
                final MapResult<MapType> result;
                if ( ! inputWrapper.isEOFMarker() ) {
                    // just skip doing anything if we don't have work to do, which is possible
                    // because we don't necessarily know how much input there is when we queue
                    // up our jobs
                    final InputType input = inputWrapper.getValue();

                    // map
                    final MapType mapValue = map.apply(input);

                    // enqueue the result into the mapResultQueue
                    result = new MapResult<MapType>(mapValue, inputWrapper.getId());

                    if ( progressFunction != null )
                        progressFunction.progress(input);
                } else {
                    // if there's no input we push empty MapResults with jobIDs for synchronization with Reducer
                    result = new MapResult<MapType>(inputWrapper.getId());
                }

                mapResultQueue.put(result);

                final int nReduced = reducer.reduceAsMuchAsPossible(mapResultQueue);
            } catch (Throwable ex) {
                errorTracker.notifyOfError(ex);
            } finally {
                // we finished a map job, release the job queue semaphore
                runningMapJobSlots.release();
            }
        }
    }
}
