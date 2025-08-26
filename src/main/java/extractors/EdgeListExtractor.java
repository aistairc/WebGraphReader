package extractors;

import it.unimi.dsi.webgraph.ImmutableGraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


// kafka generator compatible file, also removes self-edges/loops
public class EdgeListExtractor {

    private static Date currentTimestamp; // Global timestamp tracker
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");


    public static void main(String[] args) throws IOException {
        // Path to the .graph basename (without extension)
        String basePath = "/absolute/path/to/basename";
        String outputPath = basePath+"-edges.txt";
        boolean removeLoops = true;
        boolean assignEdgeWeights = true;
        // set bounds for random assignment of edge weights
        int minBound = 1;
        int maxBound = 10;
        boolean assignTimeStamps = true;
        String startingTime = "2023-01-01T00:00:00.000";
        Random rand = new Random(420); //Note: Same generator is used for both node value and time stamp assignment

        try {
            currentTimestamp = formatter.parse(startingTime);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid starting timestamp format", e);
        }


        // Load the graph
        ImmutableGraph graph = ImmutableGraph.load(basePath);
        int loopCount = 0;
        int edgeId = 0;
        String line = null;

        // Output writer
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {

            // For each node, get successors (i.e., outgoing links)
            for (int source = 0; source < graph.numNodes(); source++) {
                int[] successors = graph.successorArray(source);
                for (int target : successors) {
                    // Skip self-loops
                    if (removeLoops && (source == target)) {
                        loopCount++;
                        continue;
                    }
                    String edgeWeight = "";
                    if(assignEdgeWeights) {
                        Integer eWeight = getRandomInt(rand, minBound, maxBound);
                        edgeWeight = "," + eWeight;
                    }

                    String timestamp = "";
                    if (assignTimeStamps) {
                        timestamp = incrementTimestamp(rand, 0.0005, 0.5, 20, 21,1000);
                        timestamp = "," + timestamp;
                    }

                    line = edgeId + "," + source + "," + target + edgeWeight + timestamp;
                    writer.write(line);
                    writer.newLine();
                    edgeId++;
                }
            }

            System.out.println("Last Event:" + line);
        }

        System.out.println("Edge list written to: " + outputPath);
        System.out.println(edgeId + 1 + " edges written to file, " + loopCount + " self-edges removed." );
    }



//    /**
//     * Increment the current timestamp by a random delay from a Weibull distribution.
//     *
//     * @param k Shape parameter:
//     *          Determines how often you will get long delays by adjusting k without affecting the short ones too much.
//     *          - k < 1  => heavy-tailed delays (many short intervals, occasional long ones)
//     *          - k = 1  => exponential distribution
//     *          - k > 1  => lighter tail, delays are more tightly clustered
//     *
//     * @param scale Scale parameter (in seconds):
//     *              - Stretches or shrinks the overall delay times
//     *              - Higher values shift the distribution toward longer delays
//     *
//     * @param maxDelaySeconds Maximum delay allowed (in seconds):
//     *                        - Used as a hard cap so delays don't get unrealistically large
//     */
//    private static String incrementTimestamp(Random rand, double k, double scale, int maxDelaySeconds) {
//        double u = rand.nextDouble();
//        double delaySeconds = scale * Math.pow(-Math.log(1 - u), 1.0 / k);
//
//        int millis = Math.min(maxDelaySeconds * 1000, (int)(delaySeconds * 1000));
//        currentTimestamp = new Date(currentTimestamp.getTime() + millis);
//        return formatter.format(currentTimestamp);
//    }
    /**
     * Increments the global currentTimestamp by a random delay (short or long),
     * and returns the formatted timestamp string.
     *
     * Short delays follow an exponential distribution clustered near zero with mean shortDelayMeanMs,
     * clamped to [0, shortDelayMaxMs]. Long delays are uniform in [longDelayMinMs, longDelayMaxMs].
     *
     * @param rand              Random instance
     * @param longDelayProb     Probability of using a long delay (e.g., 0.05 for 5%)
     * @param shortDelayMeanMs  Mean of the exponential distribution for short delays (in ms)
     * @param shortDelayMaxMs   Maximum allowed short delay (in ms)
     * @param longDelayMinMs    Minimum long delay (in ms)
     * @param longDelayMaxMs    Maximum long delay (in ms)
     * @return Formatted timestamp string after incrementing currentTimestamp
     */
    private static String incrementTimestamp(Random rand, double longDelayProb,
                                             double shortDelayMeanMs, int shortDelayMaxMs,
                                             int longDelayMinMs, int longDelayMaxMs) {
        int delayMs;
//
        if (rand.nextDouble() < longDelayProb) {
            // Long delay between [longDelayMinMs, longDelayMaxMs]
            delayMs = longDelayMinMs + rand.nextInt(longDelayMaxMs - longDelayMinMs + 1);
        } else {
            // Short delay from exponential distribution, clamped to [0, shortDelayMaxMs]
            double lambda = 1.0 / shortDelayMeanMs;
            double delay = -Math.log(1 - rand.nextDouble()) / lambda;
            delayMs = (int) Math.min(delay, shortDelayMaxMs);
        }

        currentTimestamp = new Date(currentTimestamp.getTime() + delayMs);
        return formatter.format(currentTimestamp);
    }



    // Helper to get a random int between min and max inclusive
    private static int getRandomInt(Random rand, int min, int max) {
        return rand.nextInt(max - min + 1) + min;
    }



}
