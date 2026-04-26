# Metrics(document-oriented)

# Semantic search,`Recursive splitter`(chunk size: 384 tokens, 15% overlapping)

- Embedding model: `qwen3-embedding:0.6b` (1024 dimensions)
- Saving time: 806.74 s

| Top-K | Threshold | Precision | Recall | MRR | nDCG | Time (ms) | Time per question (ms) |
|-------|-----------|-----------|--------|-----|------|-----------|------------------------|
| 1     | 0.01      | 0.4661    | 0.4407 | 0.4661 | 0.4661 | 10755     | 91.14                  |
| 3     | 0.01      | 0.6653    | 0.6653 | 0.5664 | 0.5866 | 10590     | 89.75                  |
| 5     | 0.01      | 0.7669    | 0.7669 | 0.5901 | 0.6289 | 10869     | 92.11                  |
| 7     | 0.01      | 0.8051    | 0.8051 | 0.5956 | 0.6433 | 10781     | 91.36                  |


-----

- Embedding model: `qwen3-embedding:4b`(2560 dimensions)
- Saving time: ???

???

-----

- Embedding model: `bge-m3`(1024 dimensions)
- Saving time: ???

???