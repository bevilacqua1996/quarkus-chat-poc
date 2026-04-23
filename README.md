# Quarkus Chat PoC

Summary
-------
This repository is a small proof-of-concept chat application built with Quarkus. It demonstrates a minimal web UI, a WebSocket-based chat agent, and a simple RAG-style knowledge base placed in resources for experimentation.

What’s included
---------------
- Quarkus application with developer mode support (see `mvnw`)
- WebSocket chat agent and a demo front-end served from static resources
- A small RAG knowledge base for experiments: `src/main/resources/rag/code-menthoring-knowledge-base.txt`
- Dockerfiles for different runtimes under `src/main/docker` (JVM, native, legacy jar)

Key files and locations
-----------------------
- Main Java code: `src/main/java/dev/langchain4j/quarkus/workshop/`
  - `CustomerSupportAgent.java` — chat agent logic
  - `CustomerSupportAgentWebSocket.java` — WebSocket endpoints
  - `ImportmapResource.java` — frontend import map resource
- Static frontend: `src/main/resources/META-INF/resources/` (index.html, demo JS components)
- Application config: `src/main/resources/application.properties`

Development
-----------
Run Quarkus in dev mode (live reload):

```bash
./mvnw quarkus:dev
```

Build
-----
Create a JVM jar:

```bash
./mvnw package
```

Docker / Containers
-------------------
There are multiple Dockerfiles in `src/main/docker` for different packaging options (JVM, native, legacy jar). Use the one matching your target runtime.

Notes and next steps
--------------------
- The project is intentionally small and experimental. It uses static demo front-end components and a text file knowledge base for rapid prototyping.
- Next improvements typically include wiring an LLM backend, improving the RAG pipeline, and adding tests.

License
-------
No license file provided in the repo; treat this as sample code.
