# Metrics(document-oriented)

# 1. Semantic search,`TokenTextSplitter`(chunk size: 800 tokens, no overlapping)

- Embedding model: `qwen3-embedding:0.6b`(1024 dimensions)
- Search type: semantic search

Saving time: 794.75 s 

| Top-K | Threshold | Precision | Recall | MRR | nDCG | Time (ms) | Time per question (ms) |
|-------|-----------|-----------|--------|-----|------|-----------|------------------------|
| 1     | 0.01      | 0.4915    | 0.4703 | 0.4915 | 0.4915 | 10700     | 90.68                  |
| 3     | 0.01      | 0.6525    | 0.6525 | 0.5706 | 0.5862 | 10916     | 92.51                  |
| 5     | 0.01      | 0.7373    | 0.7373 | 0.5888 | 0.6210 | 10766     | 91.24                  |
| 7     | 0.01      | 0.8051    | 0.8051 | 0.5983 | 0.6455 | 10936     | 92.68                  |

-----

- Embedding model: `qwen3-embedding:4b`(2560 dimensions)
- Search type: semantic search

Saving time: 1666.79 s

| Top-K | Threshold | Precision | Recall | MRR    | nDCG   | Time (ms) |
|-------|-----------|-----------|--------|--------|--------|-----------|
| 1     | 0.01      | 0.6017    | 0.5720 | 0.6017 | 0.6017 | 23242     |
| 3     | 0.01      | 0.8305    | 0.8305 | 0.7076 | 0.7359 | 23415     |
| 5     | 0.01      | 0.8771    | 0.8771 | 0.7195 | 0.7558 | 23569     |
| 7     | 0.01      | 0.8941    | 0.8941 | 0.7219 | 0.7615 | 23537     |


-----

- Embedding model: `bge-m3`(1024 dimensions)
- Search type: semantic search

Saving time: 681.15 s

| Top-K | Threshold | Precision | Recall | MRR    | nDCG   | Time (ms) |
|-------|-----------|-----------|--------|--------|--------|-----------|
| 1     | 0.01      | 0.5593    | 0.5297 | 0.5593 | 0.5593 | 11439     |
| 3     | 0.01      | 0.7203    | 0.7203 | 0.6328 | 0.6485 | 11646     |
| 5     | 0.01      | 0.7839    | 0.7839 | 0.6463 | 0.6751 | 11958     |
| 7     | 0.01      | 0.8220    | 0.8220 | 0.6518 | 0.6887 | 12110     |