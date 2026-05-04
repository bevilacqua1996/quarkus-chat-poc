# Quarkus Chat PoC

A small proof-of-concept chat application built with Quarkus, Vaadin, and LangChain4j. The app exposes a browser UI that talks to a customer-support-style agent and streams responses through Vaadin server-side updates.

## What this project demonstrates

- A Quarkus 3 application with a custom `@QuarkusMain` entry point
- A Vaadin-based chat screen served from `src/main/java/com/jcon/ui/views/MainView.java`
- A LangChain4j AI service that answers questions in a concise, developer-friendly style
- A server-side input guardrail that blocks out-of-scope questions before they reach the model
- A simple RAG policy file in `src/main/resources/rag/code-menthoring-knowledge-base.txt`
- Server-side markdown rendering for assistant responses, including code blocks and links

## Project structure

Key files and folders:

- `src/main/java/com/jcon/Application.java`
  - Application bootstrap class
- `src/main/java/com/jcon/ui/MainView.java`
  - Main Vaadin UI used for the chat page
- `src/main/java/com/jcon/backend/CustomerSupportAgent.java`
  - LangChain4j AI service definition with MCP priority and Markdown output rules
- `src/main/java/com/jcon/backend/MentoringScopeGuardrail.java`
  - Input guardrail that blocks out-of-scope questions and preserves casual small talk
- `src/main/java/com/jcon/backend/RagIngestion.java`
  - Loads documents into the embedding store at startup
- `src/main/java/com/jcon/backend/RagRetriever.java`
  - Builds the retrieval augmentor used by the agent
- `src/main/resources/rag/code-menthoring-knowledge-base.txt`
  - Local policy text used to guide the assistant on scope, tone, and examples
- `src/main/resources/META-INF/resources/styles.css`
  - Extra styling for the Vaadin app
- `src/main/resources/application.properties`
  - Model, embedding, and RAG configuration

## Requirements

- Java 21
- Maven Wrapper included in the repo (`./mvnw`)
- Ollama running locally with the configured chat model available

The current configuration uses:

- Chat model: `qwen3:1.7b`
- Temperature: `0`
- Embedding model: `BgeSmallEnQuantizedEmbeddingModel`
- RAG document location: `src/main/resources/rag`

## Run locally

Start the application in development mode:

```bash
./mvnw quarkus:dev
```

Then open the app in your browser and use the chat UI to send a message.

## Build

Create a regular application build:

```bash
./mvnw package
```

## How it works

1. The Vaadin UI loads the chat page and initializes the server-side conversation state.
2. The guardrail checks each user message and rewrites out-of-scope requests to a sentinel value.
3. The agent checks the menthoring MCP tool first for technical questions and must base those answers on the tool results.
4. The `rag` policy file guides scope, tone, and the fallback message for unsupported questions.
5. The server renders markdown, including code snippets, into Vaadin components.

## Notes

- The project is intentionally small and experimental.
- The knowledge base is stored as plain text so it is easy to change and re-ingest.
- Logging for LangChain4j and Ollama is set to `DEBUG` in `application.properties` to make local experimentation easier.

## License

No license file is included in this repository.
