# WebGraphReader
Reads files in .graph format

Extracts scenario and node files from Webgraph formats according to KafkaDataSend/SendToFlinkPregel's input data standard.
EdgeListExtractor.java extracts the edge list as a scenario file which consists of a list of edges with
Edge ID, Source Node Id, Target Node Id, Edge Weight
Set assignEdgeWeights = true to randomly assign edge weights between the minBound and maxBound
Set removeLoops = true to remove any edges where the source node and target node are the same

NodeListExtractor.java extracts the node list as a scenario file which consists of a list of nodes with
Node ID, Block ID, Node Weight
Set assignNodeWeights = true to randomly assign node weights between the minBound and maxBound
Set assignBlockID = true to assign block IDs for each node based on their hostname. All URLs with the same hostname are assigned the same Block.
Blockstats txt file gives the number of nodes assigned for each block



