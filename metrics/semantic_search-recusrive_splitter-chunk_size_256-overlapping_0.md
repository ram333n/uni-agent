# Metrics(document-oriented)

# Semantic search,`Recursive splitter`(chunk size: 256 tokens, no overlapping)

- Embedding model: `qwen3-embedding:0.6b` (1024 dimensions)
- Saving time: 807.16 s 


| Top-K | Threshold | Precision | Recall | MRR | nDCG | Time (ms) | Time per question (ms) |
|-------|-----------|-----------|--------|-----|------|-----------|------------------------|
| 1     | 0.01      | 0.4915    | 0.4703 | 0.4915 | 0.4915 | 10700     | 90.68                  |
| 3     | 0.01      | 0.6525    | 0.6525 | 0.5706 | 0.5862 | 10916     | 92.51                  |
| 5     | 0.01      | 0.7373    | 0.7373 | 0.5888 | 0.6210 | 10766     | 91.24                  |
| 7     | 0.01      | 0.8051    | 0.8051 | 0.5983 | 0.6455 | 10936     | 92.68                  |


-----

- Embedding model: `qwen3-embedding:4b`(2560 dimensions)
- Saving time: ???

???

-----

- Embedding model: `bge-m3`(1024 dimensions)
- Saving time: ???

???