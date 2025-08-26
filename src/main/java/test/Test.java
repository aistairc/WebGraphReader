package test;

import it.unimi.dsi.webgraph.BVGraph;

public class Test {
    public static void main(String[] args) throws Exception {
        // Base names (no extensions)
        String compressedBaseName = "tiny-converted";   // name for compressed files
        compressedBaseName =  "/absolute/path/to/basename";

        // 1. Load ASCII graph (each line is a node's successors)
//        ImmutableGraph asciiGraph = ASCIIGraph.load(compressedBaseName);
//        BVGraph.loadASCII("basename");
//        new BV

        // 2. Compress and store the graph using WebGraph format
//        BVGraph.store(asciiGraph, compressedBaseName);

        // 3. Load the compressed graph
        BVGraph compressedGraph = BVGraph.load(compressedBaseName);



        // 4. Print number of nodes and their successors
        System.out.println("Number of nodes: " + compressedGraph.numNodes());
        for (int i = 0; i < compressedGraph.numNodes(); i++) {
            int[] successors = compressedGraph.successorArray(i);
            System.out.print("Node " + i + " → ");
            for (int s : successors) {
                System.out.print(s + " ");
            }
            System.out.println();
        }
    }
}