# Uni Agent

Uni Agent is a university-focused assistant project built around a Python conversational agent, a Java/Spring MCP server for faculty document search, and supporting datasets/tooling for evaluation and labeling.

## Repository structure

- `agent/` — Python Chainlit/LangGraph chat agent and orchestration logic.
- `mcp-servers/faculty-mcp-server/` — Spring Boot MCP server with semantic search, document ingestion, and PostgreSQL/pgvector integration.
- `labeling-tool/` — Flask-based dataset labeling and document preview tool.
- `test-dataset/` — test dataset files and archives used for question/evaluation flows.
- `metrics/` — benchmark and semantic-search experiment results.
- `LICENSE` — project license.
