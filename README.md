# Uni Agent

Uni Agent is a university-focused assistant project built around a Python conversational agent, a Java/Spring MCP server for faculty document search, and supporting datasets/tooling for evaluation and labeling.

## Technologies & Frameworks

- **Python** — primary language for agent and tooling components.
- **Chainlit** — chat UI/runtime for the `agent` application.
- **LangChain / LangGraph** — LLM orchestration and agent workflow logic.
- **Flask** — web framework for the dataset labeling tool.
- **Java 21** — runtime for the faculty MCP server.
- **Spring Boot** — backend framework for the MCP server.
- **Spring AI** — AI integrations for MCP server and semantic search stack.
- **PostgreSQL + pgvector** — vector-capable storage for document embeddings/search.
- **Liquibase** — database schema migration management.
- **Maven** — build and dependency management for Java services.

## Repository structure

- `agent/` — Python Chainlit/LangGraph chat agent and orchestration logic.
- `mcp-servers/faculty-mcp-server/` — Spring Boot MCP server with semantic search, document ingestion, and PostgreSQL/pgvector integration.
- `labeling-tool/` — Flask-based dataset labeling and document preview tool.
- `test-dataset/` — test dataset files and archives used for question/evaluation flows.
- `metrics/` — benchmark and semantic-search experiment results.
- `LICENSE` — project license.
