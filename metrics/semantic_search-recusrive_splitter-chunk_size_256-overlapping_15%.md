# Metrics(document-oriented)

# Semantic search,`Recursive splitter`(chunk size: 256 tokens, 15% overlapping)

- Embedding model: `qwen3-embedding:0.6b` (1024 dimensions)
- Saving time: 804.33 s

| Top-K | Threshold | Precision | Recall | MRR | nDCG | Time (ms) | Time per question (ms) |
|-------|-----------|-----------|--------|-----|------|-----------|------------------------|
| 1     | 0.01      | 0.4915    | 0.4661 | 0.4915 | 0.4915 | 10915     | 92.50                  |
| 3     | 0.01      | 0.6229    | 0.6229 | 0.5537 | 0.5658 | 10964     | 92.92                  |
| 5     | 0.01      | 0.7331    | 0.7331 | 0.5782 | 0.6122 | 10967     | 92.94                  |
| 7     | 0.01      | 0.7839    | 0.7839 | 0.5863 | 0.6305 | 10940     | 92.71                  |


-----

- Embedding model: `qwen3-embedding:4b`(2560 dimensions)
- Saving time: ???

???

-----

- Embedding model: `bge-m3`(1024 dimensions)
- Saving time: 697.25 s

| Top-K | Threshold | Precision | Recall | MRR | nDCG | Time (ms) | Time per question (ms) |
|-------|-----------|-----------|--------|-----|------|-----------|------------------------|
| 1     | 0.01      | 0.5424    | 0.5127 | 0.5424 | 0.5424 | 13050     | 110.59                 |
| 3     | 0.01      | 0.6737    | 0.6737 | 0.6073 | 0.6179 | 13120     | 111.19                 |
| 5     | 0.01      | 0.7627    | 0.7627 | 0.6290 | 0.6553 | 13149     | 111.43                 |
| 7     | 0.01      | 0.8093    | 0.8093 | 0.6368 | 0.6716 | 13183     | 111.72                 |