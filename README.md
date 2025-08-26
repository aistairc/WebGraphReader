# WebGraphReader

**WebGraphReader** is a utility for reading `.graph` format files and extracting scenario and node files based on the expected input standards for `KafkaDataSend` / `SendToFlinkPregel`.

## Features

The tool provides two main extractors:



### EdgeListExtractor.java

Extracts the **edge list** as a scenario file. Each entry contains:

- `Edge ID`
- `Source Node ID`
- `Target Node ID`
- `Edge Weight`

#### Configuration Options:

- `assignEdgeWeights = true`: Randomly assigns edge weights between `minBound` and `maxBound`.
- `removeLoops = true`: Removes any self-loops (edges where source and target nodes are the same).



### NodeListExtractor.java

Extracts the **node list** as a scenario file. Each entry contains:

- `Node ID`
- `Block ID`
- `Node Weight`

#### Configuration Options:

- `assignNodeWeights = true`: Randomly assigns node weights between `minBound` and `maxBound`.
- `assignBlockID = true`: Assigns `Block ID`s based on the hostname in the URL. All URLs with the same hostname are assigned the same block.

> A `blockstats.txt` file is also generated, summarizing the number of nodes per block.

---
