# Metrics(document-oriented)

# 1. Semantic search,`TokenTextSplitter`(chunk size: 800 tokens, no overlapping)

- Embedding model: `qwen3-embedding:0.6b`(1024 dimensions)
- Search type: semantic search

Saving time: 794.75 s 

| Top-K | Threshold | Precision | Recall | MRR    | nDCG   | Time (ms) |
|-------|-----------|-----------|--------|--------|--------|-----------|
| 1     | 0.01      | 0.5847    | 0.5593 | 0.5847 | 0.5847 | 8585      |
| 3     | 0.01      | 0.7203    | 0.7203 | 0.6511 | 0.6624 | 9972      |
| 5     | 0.01      | 0.7712    | 0.7712 | 0.6638 | 0.6843 | 9995      |
| 7     | 0.01      | 0.8051    | 0.8051 | 0.6675 | 0.6965 | 10108     |

-----

- Embedding model: `qwen3-embedding:4b`(2560 dimensions)
- Search type: semantic search

Saving time: 1666.79 s

Тепер це в markdown:

| Top-K | Threshold | Precision | Recall | MRR    | nDCG   | Time (ms) |
|-------|-----------|-----------|--------|--------|--------|-----------|
| 1     | 0.01      | 0.6017    | 0.5720 | 0.6017 | 0.6017 | 23242     |
| 3     | 0.01      | 0.8305    | 0.8305 | 0.7076 | 0.7359 | 23415     |
| 5     | 0.01      | 0.8771    | 0.8771 | 0.7195 | 0.7558 | 23569     |
| 7     | 0.01      | 0.8941    | 0.8941 | 0.7219 | 0.7615 | 23537     |