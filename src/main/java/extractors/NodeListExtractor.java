package extractors;

import it.unimi.dsi.fastutil.io.BinIO;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class NodeListExtractor {

    public static void main(String[] args) throws Exception {
        // Path to the .graph basename (without extension)
        String basePath = "/absolute/path/to/basename";
        String fclPath = basePath + ".fcl";
        String nodesFilePath = basePath + "-nodes.txt";
        String blockStatsPath = basePath + "-blockstats.txt";
        boolean assignNodeWeights = true;
        boolean assignBlockID = true;
        Random rand = new Random(123);
        int minBound = 1;
        int maxBound = 50;

        // Load nodeID -> URL mapping from .fcl
        @SuppressWarnings("unchecked")
        List<? extends CharSequence> node2url = (List<? extends CharSequence>) BinIO.loadObject(fclPath);

        // Maps hostname -> block ID
        Map<String, String> hostnameToBlockId = new HashMap<>();
        // Maps block ID -> count of nodes
        Map<String, Integer> blockCounts = new HashMap<>();
        // Output lines
        List<String> outputLines = new ArrayList<>();

        int blockCounter = 1;

        for (int nodeId = 0; nodeId < node2url.size(); nodeId++) {
            CharSequence urlString = node2url.get(nodeId);
            String hostname = extractHostname(urlString.toString());

            if (hostname == null) {
                System.out.println("Unknown host name: " + urlString);
                hostname = "unknown";
            }

            // Assign block ID if not already assigned
            String blockId = "";
            if (assignBlockID) {
                if (hostnameToBlockId.containsKey(hostname)) {
                    blockId = hostnameToBlockId.get(hostname);
                } else {
                    blockId = "b-" + blockCounter;
                    hostnameToBlockId.put(hostname, blockId);
                    blockCounter++;
                }

                blockCounts.put(blockId, blockCounts.getOrDefault(blockId, 0) + 1);
                blockId = "," + blockId;

            }

            // Generate random value between minBound and maxBound inclusive
            // User-defined bounds for random value (change as needed)
            String nodeWeight = ",";
            if (assignNodeWeights)
            {
                Integer nWeight = getRandomInt(rand, minBound, maxBound);
                nodeWeight  = nodeWeight + nWeight;
            }

            String outputLine = nodeId  + blockId  + nodeWeight;
            // Prepare output line
            outputLines.add(outputLine);
        }

        // Write to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nodesFilePath))) {
            for (String line : outputLines) {
                writer.write(line);
                writer.newLine();
            }
        }


        // Write block statistics
        try (BufferedWriter statWriter = new BufferedWriter(new FileWriter(blockStatsPath))) {
            blockCounts.entrySet().stream()
                    .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue())) // sort by blockSize
                    .forEach(entry -> {
                        try {
                            statWriter.write(entry.getKey() + "," + entry.getValue());
                            statWriter.newLine();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }

        System.out.println(node2url.size() + " nodes written to: " + nodesFilePath);
        System.out.println("Block statistics written to: " + blockStatsPath);

    }

    // Helper to get a random int between min and max inclusive
    private static int getRandomInt(Random rand, int min, int max) {
        return rand.nextInt(max - min + 1) + min;
    }

    // Helper: extract hostname from URL
    private static String extractHostname(String urlString) {
        try {
            // Handle missing schemes (rare but possible)
            if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
                urlString = "http://" + urlString;
            }
            URL url = new URL(urlString);
            return url.getHost();
        } catch (MalformedURLException e) {
            // Return null if URL is broken
            System.out.println("Malformed URL: " + e);
            return null;
        }
    }
}
