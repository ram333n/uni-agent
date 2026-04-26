# Metrics(document-oriented)

# Semantic search,`Recursive splitter`(chunk size: 512 tokens, no overlapping)

- Embedding model: `qwen3-embedding:0.6b` (1024 dimensions)
- Saving time: 786.03 s

| Top-K | Threshold | Precision | Recall | MRR | nDCG | Time (ms) | Time per question (ms) |
|-------|-----------|-----------|--------|-----|------|-----------|------------------------|
| 1     | 0.01      | 0.5424    | 0.5085 | 0.5424 | 0.5424 | 10346     | 87.68                  |
| 3     | 0.01      | 0.6737    | 0.6737 | 0.6073 | 0.6165 | 10467     | 88.70                  |
| 5     | 0.01      | 0.7500    | 0.7500 | 0.6230 | 0.6489 | 10634     | 90.12                  |
| 7     | 0.01      | 0.8008    | 0.8008 | 0.6309 | 0.6664 | 10632     | 90.10                  |

-----

- Embedding model: `qwen3-embedding:4b`(2560 dimensions)
- Search time: ???




-----

- Embedding model: `bge-m3`(1024 dimensions)
- Search time: ???
