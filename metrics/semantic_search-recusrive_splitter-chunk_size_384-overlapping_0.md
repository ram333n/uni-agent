# Metrics(document-oriented)

# Semantic search,`Recursive splitter`(chunk size: 384 tokens, no overlapping)

- Embedding model: `qwen3-embedding:0.6b` (1024 dimensions)
- Saving time: 795.35 s

| Top-K | Threshold | Precision | Recall | MRR | nDCG | Time (ms) | Time per question (ms) |
|-------|-----------|-----------|--------|-----|------|-----------|------------------------|
| 1     | 0.01      | 0.5000    | 0.4661 | 0.5000 | 0.5000 | 10680     | 90.51                  |
| 3     | 0.01      | 0.6864    | 0.6864 | 0.5904 | 0.6109 | 10717     | 90.82                  |
| 5     | 0.01      | 0.7415    | 0.7415 | 0.6044 | 0.6344 | 10733     | 90.96                  |
| 7     | 0.01      | 0.7712    | 0.7712 | 0.6084 | 0.6450 | 10784     | 91.39                  |


-----

- Embedding model: `qwen3-embedding:4b`(2560 dimensions)
- Search time: ???




-----

- Embedding model: `bge-m3`(1024 dimensions)
- Search time: ???
