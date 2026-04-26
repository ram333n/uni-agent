# Metrics(document-oriented)

# Semantic search,`Recursive splitter`(chunk size: 512 tokens, 15% overlapping)

- Embedding model: `qwen3-embedding:0.6b` (1024 dimensions)
- Saving time: 811.09 s

| Top-K | Threshold | Precision | Recall | MRR | nDCG | Time (ms) | Time per question (ms) |
|-------|-----------|-----------|--------|-----|------|-----------|------------------------|
| 1     | 0.01      | 0.4915    | 0.4619 | 0.4915 | 0.4915 | 10368     | 87.86                  |
| 3     | 0.01      | 0.6695    | 0.6695 | 0.5763 | 0.5946 | 10478     | 88.80                  |
| 5     | 0.01      | 0.7458    | 0.7458 | 0.5941 | 0.6263 | 10531     | 89.25                  |
| 7     | 0.01      | 0.8051    | 0.8051 | 0.6031 | 0.6467 | 10692     | 90.61                  |

-----

- Embedding model: `qwen3-embedding:4b`(2560 dimensions)
- Saving time: ???

???

-----

- Embedding model: `bge-m3`(1024 dimensions)
- Saving time: 680.35 s

| Top-K | Threshold | Precision | Recall | MRR | nDCG | Time (ms) | Time per question (ms) |
|-------|-----------|-----------|--------|-----|------|-----------|------------------------|
| 1     | 0.01      | 0.5085    | 0.4788 | 0.5085 | 0.5085 | 12159     | 103.04                 |
| 3     | 0.01      | 0.7542    | 0.7542 | 0.6144 | 0.6444 | 12276     | 104.03                 |
| 5     | 0.01      | 0.8347    | 0.8347 | 0.6314 | 0.6776 | 12416     | 105.22                 |
| 7     | 0.01      | 0.8432    | 0.8432 | 0.6328 | 0.6807 | 12511     | 106.03                 |