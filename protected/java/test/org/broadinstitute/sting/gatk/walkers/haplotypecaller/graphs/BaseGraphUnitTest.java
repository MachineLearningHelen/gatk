/*
*  By downloading the PROGRAM you agree to the following terms of use:
*  
*  BROAD INSTITUTE - SOFTWARE LICENSE AGREEMENT - FOR ACADEMIC NON-COMMERCIAL RESEARCH PURPOSES ONLY
*  
*  This Agreement is made between the Broad Institute, Inc. with a principal address at 7 Cambridge Center, Cambridge, MA 02142 (BROAD) and the LICENSEE and is effective at the date the downloading is completed (EFFECTIVE DATE).
*  
*  WHEREAS, LICENSEE desires to license the PROGRAM, as defined hereinafter, and BROAD wishes to have this PROGRAM utilized in the public interest, subject only to the royalty-free, nonexclusive, nontransferable license rights of the United States Government pursuant to 48 CFR 52.227-14; and
*  WHEREAS, LICENSEE desires to license the PROGRAM and BROAD desires to grant a license on the following terms and conditions.
*  NOW, THEREFORE, in consideration of the promises and covenants made herein, the parties hereto agree as follows:
*  
*  1. DEFINITIONS
*  1.1 PROGRAM shall mean copyright in the object code and source code known as GATK2 and related documentation, if any, as they exist on the EFFECTIVE DATE and can be downloaded from http://www.broadinstitute/GATK on the EFFECTIVE DATE.
*  
*  2. LICENSE
*  2.1   Grant. Subject to the terms of this Agreement, BROAD hereby grants to LICENSEE, solely for academic non-commercial research purposes, a non-exclusive, non-transferable license to: (a) download, execute and display the PROGRAM and (b) create bug fixes and modify the PROGRAM. 
*  The LICENSEE may apply the PROGRAM in a pipeline to data owned by users other than the LICENSEE and provide these users the results of the PROGRAM provided LICENSEE does so for academic non-commercial purposes only.  For clarification purposes, academic sponsored research is not a commercial use under the terms of this Agreement.
*  2.2  No Sublicensing or Additional Rights. LICENSEE shall not sublicense or distribute the PROGRAM, in whole or in part, without prior written permission from BROAD.  LICENSEE shall ensure that all of its users agree to the terms of this Agreement.  LICENSEE further agrees that it shall not put the PROGRAM on a network, server, or other similar technology that may be accessed by anyone other than the LICENSEE and its employees and users who have agreed to the terms of this agreement.
*  2.3  License Limitations. Nothing in this Agreement shall be construed to confer any rights upon LICENSEE by implication, estoppel, or otherwise to any computer software, trademark, intellectual property, or patent rights of BROAD, or of any other entity, except as expressly granted herein. LICENSEE agrees that the PROGRAM, in whole or part, shall not be used for any commercial purpose, including without limitation, as the basis of a commercial software or hardware product or to provide services. LICENSEE further agrees that the PROGRAM shall not be copied or otherwise adapted in order to circumvent the need for obtaining a license for use of the PROGRAM.  
*  
*  3. OWNERSHIP OF INTELLECTUAL PROPERTY 
*  LICENSEE acknowledges that title to the PROGRAM shall remain with BROAD. The PROGRAM is marked with the following BROAD copyright notice and notice of attribution to contributors. LICENSEE shall retain such notice on all copies.  LICENSEE agrees to include appropriate attribution if any results obtained from use of the PROGRAM are included in any publication.
*  Copyright 2012 Broad Institute, Inc.
*  Notice of attribution:  The GATK2 program was made available through the generosity of Medical and Population Genetics program at the Broad Institute, Inc.
*  LICENSEE shall not use any trademark or trade name of BROAD, or any variation, adaptation, or abbreviation, of such marks or trade names, or any names of officers, faculty, students, employees, or agents of BROAD except as states above for attribution purposes.
*  
*  4. INDEMNIFICATION
*  LICENSEE shall indemnify, defend, and hold harmless BROAD, and their respective officers, faculty, students, employees, associated investigators and agents, and their respective successors, heirs and assigns, (Indemnitees), against any liability, damage, loss, or expense (including reasonable attorneys fees and expenses) incurred by or imposed upon any of the Indemnitees in connection with any claims, suits, actions, demands or judgments arising out of any theory of liability (including, without limitation, actions in the form of tort, warranty, or strict liability and regardless of whether such action has any factual basis) pursuant to any right or license granted under this Agreement.
*  
*  5. NO REPRESENTATIONS OR WARRANTIES
*  THE PROGRAM IS DELIVERED AS IS.  BROAD MAKES NO REPRESENTATIONS OR WARRANTIES OF ANY KIND CONCERNING THE PROGRAM OR THE COPYRIGHT, EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NONINFRINGEMENT, OR THE ABSENCE OF LATENT OR OTHER DEFECTS, WHETHER OR NOT DISCOVERABLE. BROAD EXTENDS NO WARRANTIES OF ANY KIND AS TO PROGRAM CONFORMITY WITH WHATEVER USER MANUALS OR OTHER LITERATURE MAY BE ISSUED FROM TIME TO TIME.
*  IN NO EVENT SHALL BROAD OR ITS RESPECTIVE DIRECTORS, OFFICERS, EMPLOYEES, AFFILIATED INVESTIGATORS AND AFFILIATES BE LIABLE FOR INCIDENTAL OR CONSEQUENTIAL DAMAGES OF ANY KIND, INCLUDING, WITHOUT LIMITATION, ECONOMIC DAMAGES OR INJURY TO PROPERTY AND LOST PROFITS, REGARDLESS OF WHETHER BROAD SHALL BE ADVISED, SHALL HAVE OTHER REASON TO KNOW, OR IN FACT SHALL KNOW OF THE POSSIBILITY OF THE FOREGOING.
*  
*  6. ASSIGNMENT
*  This Agreement is personal to LICENSEE and any rights or obligations assigned by LICENSEE without the prior written consent of BROAD shall be null and void.
*  
*  7. MISCELLANEOUS
*  7.1 Export Control. LICENSEE gives assurance that it will comply with all United States export control laws and regulations controlling the export of the PROGRAM, including, without limitation, all Export Administration Regulations of the United States Department of Commerce. Among other things, these laws and regulations prohibit, or require a license for, the export of certain types of software to specified countries.
*  7.2 Termination. LICENSEE shall have the right to terminate this Agreement for any reason upon prior written notice to BROAD. If LICENSEE breaches any provision hereunder, and fails to cure such breach within thirty (30) days, BROAD may terminate this Agreement immediately. Upon termination, LICENSEE shall provide BROAD with written assurance that the original and all copies of the PROGRAM have been destroyed, except that, upon prior written authorization from BROAD, LICENSEE may retain a copy for archive purposes.
*  7.3 Survival. The following provisions shall survive the expiration or termination of this Agreement: Articles 1, 3, 4, 5 and Sections 2.2, 2.3, 7.3, and 7.4.
*  7.4 Notice. Any notices under this Agreement shall be in writing, shall specifically refer to this Agreement, and shall be sent by hand, recognized national overnight courier, confirmed facsimile transmission, confirmed electronic mail, or registered or certified mail, postage prepaid, return receipt requested.  All notices under this Agreement shall be deemed effective upon receipt. 
*  7.5 Amendment and Waiver; Entire Agreement. This Agreement may be amended, supplemented, or otherwise modified only by means of a written instrument signed by all parties. Any waiver of any rights or failure to act in a specific instance shall relate only to such instance and shall not be construed as an agreement to waive any rights or fail to act in any other instance, whether or not similar. This Agreement constitutes the entire agreement among the parties with respect to its subject matter and supersedes prior agreements or understandings between the parties relating to its subject matter. 
*  7.6 Binding Effect; Headings. This Agreement shall be binding upon and inure to the benefit of the parties and their respective permitted successors and assigns. All headings are for convenience only and shall not affect the meaning of any provision of this Agreement.
*  7.7 Governing Law. This Agreement shall be construed, governed, interpreted and applied in accordance with the internal laws of the Commonwealth of Massachusetts, U.S.A., without regard to conflict of laws principles.
*/

package org.broadinstitute.sting.gatk.walkers.haplotypecaller.graphs;

import org.broadinstitute.sting.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import scala.actors.threadpool.Arrays;

import java.io.File;
import java.util.*;

public class BaseGraphUnitTest extends BaseTest {
    SeqGraph graph;
    SeqVertex v1, v2, v3, v4, v5;

    @BeforeMethod
    public void setUp() throws Exception {
        graph = new SeqGraph();

        v1 = new SeqVertex("A");
        v2 = new SeqVertex("C");
        v3 = new SeqVertex("C");
        v4 = new SeqVertex("C");
        v5 = new SeqVertex("C");

        graph.addVertices(v1, v2, v3, v4, v5);
        graph.addEdge(v1, v2);
        graph.addEdge(v2, v4);
        graph.addEdge(v3, v2);
        graph.addEdge(v2, v3);
        graph.addEdge(v4, v5);
    }

    @Test
    public void testIncomingAndOutgoingVertices() throws Exception {
        assertVertexSetEquals(graph.outgoingVerticesOf(v1), v2);
        assertVertexSetEquals(graph.incomingVerticesOf(v1));

        assertVertexSetEquals(graph.outgoingVerticesOf(v2), v3, v4);
        assertVertexSetEquals(graph.incomingVerticesOf(v2), v1, v3);

        assertVertexSetEquals(graph.outgoingVerticesOf(v3), v2);
        assertVertexSetEquals(graph.incomingVerticesOf(v3), v2);

        assertVertexSetEquals(graph.outgoingVerticesOf(v4), v5);
        assertVertexSetEquals(graph.incomingVerticesOf(v4), v2);

        assertVertexSetEquals(graph.outgoingVerticesOf(v5));
        assertVertexSetEquals(graph.incomingVerticesOf(v5), v4);
    }

    @Test
         public void testRemoveSingletonOrphanVertices() throws Exception {
        // all vertices in graph are connected
        final List<SeqVertex> kept = new LinkedList<SeqVertex>(graph.vertexSet());
        final SeqVertex rm1 = new SeqVertex("CAGT");
        final SeqVertex rm2 = new SeqVertex("AGTC");
        graph.addVertices(rm1, rm2);
        Assert.assertEquals(graph.vertexSet().size(), kept.size() + 2);
        final BaseEdge rm12e = new BaseEdge(false, 1);
        graph.addEdge(rm1, rm2, rm12e);

        final SeqGraph original = (SeqGraph)graph.clone();
        graph.removeSingletonOrphanVertices();
        Assert.assertTrue(BaseGraph.graphEquals(original, graph), "Graph with disconnected component but edges between components shouldn't be modified");

        graph.removeEdge(rm12e); // now we should be able to remove rm1 and rm2
        graph.removeSingletonOrphanVertices();
        Assert.assertTrue(graph.vertexSet().containsAll(kept));
        Assert.assertFalse(graph.containsVertex(rm1));
        Assert.assertFalse(graph.containsVertex(rm2));
    }

    @Test
    public void testRemovePathsNotConnectedToRef() throws Exception {
        final SeqGraph graph = new SeqGraph();

        SeqVertex src = new SeqVertex("A");
        SeqVertex end = new SeqVertex("A");
        SeqVertex g1 = new SeqVertex("C");
        SeqVertex g2 = new SeqVertex("G");
        SeqVertex g3 = new SeqVertex("T");
        SeqVertex g4 = new SeqVertex("AA");
        SeqVertex g5 = new SeqVertex("AA");
        SeqVertex g6 = new SeqVertex("AA");
        SeqVertex g8 = new SeqVertex("AA");
        SeqVertex g7 = new SeqVertex("AA");
        SeqVertex b1 = new SeqVertex("CC");
        SeqVertex b2 = new SeqVertex("GG");
        SeqVertex b3 = new SeqVertex("TT");
        SeqVertex b4 = new SeqVertex("AAA");
        SeqVertex b5 = new SeqVertex("CCC");
        SeqVertex b6 = new SeqVertex("GGG");
        SeqVertex b7 = new SeqVertex("AAAA");
        SeqVertex b8 = new SeqVertex("GGGG");
        SeqVertex b9 = new SeqVertex("CCCC");

        graph.addVertices(src, end, g1, g2, g3, g4, g5, g6, g7, g8);
        graph.addEdges(new BaseEdge(true, 1), src, g1, g2, g4, end);
        graph.addEdges(src, g1, g5, g6, g7, end);
        graph.addEdges(src, g1, g5, g8, g7, end);
        graph.addEdges(src, g1, g3, end);

        // the current state of the graph is the good one
        final SeqGraph good = (SeqGraph)graph.clone();

        // now add the bads to the graph
        graph.addVertices(b1, b2, b3, b4, b5, b6, b7, b8, b9);
        graph.addEdges(src, b1); // source -> b1 is dead
        graph.addEdges(b6, src); // x -> source is bad
        graph.addEdges(g4, b2); // off random vertex is bad
        graph.addEdges(g3, b3, b4); // two vertices that don't connect to end are bad
        graph.addEdges(end, b5); // vertex off end is bad
        graph.addEdges(g3, b7, b8, b7); // cycle is bad
        graph.addEdges(g3, b9, b9); // self-cycle is bad

        final boolean debug = false;
        if ( debug ) good.printGraph(new File("expected.dot"), 0);
        if ( debug ) graph.printGraph(new File("bad.dot"), 0);
        graph.removePathsNotConnectedToRef();
        if ( debug ) graph.printGraph(new File("actual.dot"), 0);

        Assert.assertTrue(BaseGraph.graphEquals(graph, good), "Failed to remove exactly the bad nodes");
    }

    @Test
    public void testRemoveVerticesNotConnectedToRefRegardlessOfEdgeDirection() throws Exception {
        final SeqGraph graph = new SeqGraph();

        SeqVertex src = new SeqVertex("A");
        SeqVertex end = new SeqVertex("A");
        SeqVertex g1 = new SeqVertex("C");
        SeqVertex g2 = new SeqVertex("G");
        SeqVertex g3 = new SeqVertex("T");
        SeqVertex g4 = new SeqVertex("AA");
        SeqVertex g5 = new SeqVertex("AA");
        SeqVertex g6 = new SeqVertex("AA");
        SeqVertex g8 = new SeqVertex("AA");
        SeqVertex g7 = new SeqVertex("AA");
        SeqVertex gPrev = new SeqVertex("AA");
        SeqVertex gPrev1 = new SeqVertex("AA");
        SeqVertex gPrev2 = new SeqVertex("AA");
        SeqVertex gAfter = new SeqVertex("AA");
        SeqVertex gAfter1 = new SeqVertex("AA");
        SeqVertex gAfter2 = new SeqVertex("AA");
        SeqVertex b1 = new SeqVertex("CC");
        SeqVertex b2 = new SeqVertex("GG");
        SeqVertex b3 = new SeqVertex("TT");
        SeqVertex b4 = new SeqVertex("AAA");
        SeqVertex b5 = new SeqVertex("CCC");
        SeqVertex b6 = new SeqVertex("GGG");

        graph.addVertices(src, end, g1, g2, g3, g4, g5, g6, g7, g8, gPrev, gPrev1, gPrev2, gAfter, gAfter1, gAfter2);
        graph.addEdges(new BaseEdge(true, 1), src, g1, g2, g4, end);
        graph.addEdges(src, g1, g5, g6, g7, end);
        graph.addEdges(src, g1, g5, g8, g7, end);
        graph.addEdges(src, g1, g3, end);

        // these should be kept, but are in the wrong direction
        graph.addEdges(gPrev, src);
        graph.addEdges(gPrev1, gPrev2, src);
        graph.addEdges(end, gAfter);
        graph.addEdges(end, gAfter1, gAfter2);

        // the current state of the graph is the good one
        final SeqGraph good = (SeqGraph)graph.clone();

        // now add the bads to the graph
        graph.addVertices(b1, b2, b3, b4, b5, b6);
        graph.addEdges(b2, b3); // b2 -> b3
        graph.addEdges(b4, b5, b4); // cycle
        graph.addEdges(b6, b6); // isolated self cycle

        final boolean debug = false;
        if ( debug ) good.printGraph(new File("expected.dot"), 0);
        if ( debug ) graph.printGraph(new File("bad.dot"), 0);
        graph.removeVerticesNotConnectedToRefRegardlessOfEdgeDirection();
        if ( debug ) graph.printGraph(new File("actual.dot"), 0);

        Assert.assertTrue(BaseGraph.graphEquals(graph, good), "Failed to remove exactly the bad nodes");
    }

    @Test
    public void testPrintEmptyGraph() throws Exception {
        final File tmp = File.createTempFile("tmp", "dot");
        tmp.deleteOnExit();
        new SeqGraph().printGraph(tmp, 10);
        new DeBruijnGraph().printGraph(tmp, 10);
    }

    @Test
    public void testComplexGraph() throws Exception {
        final File tmp = File.createTempFile("tmp", "dot");
        tmp.deleteOnExit();
        graph.printGraph(tmp, 10);
    }

    private void assertVertexSetEquals(final Collection<SeqVertex> actual, final SeqVertex ... expected) {
        final Set<SeqVertex> actualSet = new HashSet<SeqVertex>(actual);
        Assert.assertEquals(actualSet.size(), actual.size(), "Duplicate elements found in vertex list");
        final Set<SeqVertex> expectedSet = expected == null ? Collections.<SeqVertex>emptySet() : new HashSet<SeqVertex>(Arrays.asList(expected));
        Assert.assertEquals(actualSet, expectedSet);
    }

    @Test(enabled = true)
    public void testPruneGraph() {
        DeBruijnGraph graph = new DeBruijnGraph();
        DeBruijnGraph expectedGraph = new DeBruijnGraph();

        DeBruijnVertex v = new DeBruijnVertex("ATGG");
        DeBruijnVertex v2 = new DeBruijnVertex("ATGGA");
        DeBruijnVertex v3 = new DeBruijnVertex("ATGGT");
        DeBruijnVertex v4 = new DeBruijnVertex("ATGGG");
        DeBruijnVertex v5 = new DeBruijnVertex("ATGGC");
        DeBruijnVertex v6 = new DeBruijnVertex("ATGGCCCCCC");

        graph.addVertex(v);
        graph.addVertex(v2);
        graph.addVertex(v3);
        graph.addVertex(v4);
        graph.addVertex(v5);
        graph.addVertex(v6);
        graph.addEdge(v, v2, new BaseEdge(false, 1));
        graph.addEdge(v2, v3, new BaseEdge(false, 3));
        graph.addEdge(v3, v4, new BaseEdge(false, 5));
        graph.addEdge(v4, v5, new BaseEdge(false, 3));
        graph.addEdge(v5, v6, new BaseEdge(false, 2));

        expectedGraph.addVertex(v2);
        expectedGraph.addVertex(v3);
        expectedGraph.addVertex(v4);
        expectedGraph.addVertex(v5);
        expectedGraph.addEdge(v2, v3, new BaseEdge(false, 3));
        expectedGraph.addEdge(v3, v4, new BaseEdge(false, 5));
        expectedGraph.addEdge(v4, v5, new BaseEdge(false, 3));

        graph.pruneGraph(2);

        Assert.assertTrue(BaseGraph.graphEquals(graph, expectedGraph));

        graph = new DeBruijnGraph();
        expectedGraph = new DeBruijnGraph();

        graph.addVertex(v);
        graph.addVertex(v2);
        graph.addVertex(v3);
        graph.addVertex(v4);
        graph.addVertex(v5);
        graph.addVertex(v6);
        graph.addEdge(v, v2, new BaseEdge(true, 1));
        graph.addEdge(v2, v3, new BaseEdge(false, 3));
        graph.addEdge(v3, v4, new BaseEdge(false, 5));
        graph.addEdge(v4, v5, new BaseEdge(false, 3));

        expectedGraph.addVertex(v);
        expectedGraph.addVertex(v2);
        expectedGraph.addVertex(v3);
        expectedGraph.addVertex(v4);
        expectedGraph.addVertex(v5);
        expectedGraph.addEdge(v, v2, new BaseEdge(true, 1));
        expectedGraph.addEdge(v2, v3, new BaseEdge(false, 3));
        expectedGraph.addEdge(v3, v4, new BaseEdge(false, 5));
        expectedGraph.addEdge(v4, v5, new BaseEdge(false, 3));

        graph.pruneGraph(2);

        Assert.assertTrue(BaseGraph.graphEquals(graph, expectedGraph));
    }
}