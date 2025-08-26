package test;

import it.unimi.dsi.webgraph.ImmutableGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoopDetector {

    public static void main(String[] args) throws IOException {
        // Path to the .graph basename (without extension)
        String basePath = "/absolute/path/to/basename";

        // Load graph
        ImmutableGraph graph = ImmutableGraph.load(basePath);

        // List to store nodes that have loops
        List<Integer> nodesWithLoops = new ArrayList<>();

        // Loop through each node
        for (int node = 0; node < graph.numNodes(); node++) {
            int[] successors = graph.successorArray(node);

            for (int target : successors) {
                if (target == node) {
                    nodesWithLoops.add(node);
                    break; // Only need to record it once
                }
            }
        }

        // Print loop info
        if (nodesWithLoops.isEmpty()) {
            System.out.println("No loops found in the graph.");
        } else {
            System.out.println("Loops detected at the following nodes:");
            for (int nodeId : nodesWithLoops) {
                System.out.println("Node " + nodeId + " has a loop.");
            }
            System.out.println("Total loops detected: " + nodesWithLoops.size() +"/" + graph.numNodes() + " nodes");
        }
    }
}
