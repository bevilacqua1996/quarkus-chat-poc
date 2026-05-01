# Quarkus Chat PoC

A small proof-of-concept chat application built with Quarkus, Vaadin, and LangChain4j. The app exposes a browser UI that talks to a customer-support-style agent and streams responses through Vaadin server-side updates.

## What this project demonstrates

- A Quarkus 3 application with a custom `@QuarkusMain` entry point
- A Vaadin-based chat screen served from `src/main/java/com/jcon/ui/MainView.java`
- A LangChain4j AI service that answers questions in a concise, developer-friendly style
- A simple RAG pipeline that loads local documents from `src/main/resources/rag`
- Server-side markdown rendering for assistant responses, including code blocks and links

## Project structure

Key files and folders:

- `src/main/java/com/jcon/Application.java`
  - Application bootstrap class
- `src/main/java/com/jcon/ui/MainView.java`
  - Main Vaadin UI used for the chat page
- `src/main/java/com/jcon/backend/CustomerSupportAgent.java`
  - LangChain4j AI service definition
- `src/main/java/com/jcon/backend/RagIngestion.java`
  - Loads documents into the embedding store at startup
- `src/main/java/com/jcon/backend/RagRetriever.java`
  - Builds the retrieval augmentor used by the agent
- `src/main/resources/rag/code-menthoring-knowledge-base.txt`
  - Local knowledge base used for retrieval experiments
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
2. When the app starts, `RagIngestion` loads documents from `src/main/resources/rag` into the embedding store.
3. User messages are sent directly to `CustomerSupportAgent` from the Vaadin view.
4. The agent uses retrieval-augmented context to generate a streamed answer.
5. The server renders markdown, including code snippets, into Vaadin components.

## Notes

- The project is intentionally small and experimental.
- The knowledge base is stored as plain text so it is easy to change and re-ingest.
- Logging for LangChain4j and Ollama is set to `DEBUG` in `application.properties` to make local experimentation easier.

## License

No license file is included in this repository.
