# first, build the test system with "ant test.init test.compile" as benchmarking code should live in test

# assuming that goes well, you can run your benchmarks by explicitly executing java with a classpath
# to the testclasses and the GATK jar, giving it the fully qualified name of the your benchmarking
# system: e.g., org.broadinstitute.sting.utils.pileup.FragmentPileupBenchmark
java -Xmx2g -cp "build/java/testclasses:lib/caliper-0.5-rc1.jar:dist/GenomeAnalysisTK.jar" com.google.caliper.Runner -Jmemory='-Xmx1g' $*