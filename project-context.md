---
project_name: 'Quarkus Chat PoC'
user_name: 'user'
date: '2026-06-10'
sections_completed: ['technology_stack', 'language_specific_rules', 'framework_specific_rules', 'testing_rules', 'code_quality_style_rules', 'development_workflow_rules', 'critical_dont_miss_rules']
existing_patterns_found: 1
status: 'complete'
workflow_state: 'complete'
rule_count: 57
optimized_for_llm: true
external_context:
  - '../PersonalMCPServer'
---

# Project Context for AI Agents

_This file contains critical rules and patterns that AI agents must follow when implementing code in this project. Focus on unobvious details that agents might otherwise miss._

---

## Technology Stack & Versions

- Java 21.
- Quarkus `3.34.3`.
- Maven Wrapper for `quarkus-chat-poc`; use `./mvnw quarkus:dev` and `./mvnw package`.
- Vaadin `25.1.3` through `vaadin-quarkus-extension`; UI is Vaadin Flow server-side Java, not hand-written frontend JavaScript.
- Quarkus LangChain4j `1.9.0.CR2`.
- Ollama chat model `qwen3:1.7b` with temperature `0`.
- Embedding provider: `dev.langchain4j.model.embedding.onnx.bgesmallenq.BgeSmallEnQuantizedEmbeddingModel`.
- PGVector embedding dimension is `384`.
- RAG documents live under `src/main/resources/rag`.
- MCP client config points to `menthoring` over streamable HTTP at `http://localhost:8081/api/mcp`.
- Sibling dependency: `../PersonalMCPServer`, a Java 21 / Quarkus `3.34.3` Gradle project using LangChain4j MCP `1.13.0-beta23`.
- `PersonalMCPServer` runs on port `8081` and exposes MCP tools `list-repos`, `describe-repo`, and `menthoring`.

## Critical Implementation Rules

### Language-Specific Rules

- Write application code in Java 21 and keep compiler parameter metadata enabled; both Maven and Gradle builds use `-parameters`.
- Keep CDI scope annotations intentional: the chat agent is `@SessionScoped`, while shared backend services/producers are `@ApplicationScoped`.
- Use constructor injection in Vaadin views where dependencies are route/view scoped; use CDI producer methods for framework objects such as `RetrievalAugmentor`.
- Preserve streaming response code as Mutiny `Multi<String>` from the AI service. UI updates from stream callbacks must run inside `UI.access(...)`.
- Protect chat state with `AtomicBoolean` or equivalent thread-safe coordination when callbacks can arrive off the UI thread.
- Prefer small, explicit Java classes for Vaadin components. Component classes should own their own CSS class name and `@StyleSheet` import when they have dedicated styling.
- Keep Markdown rendering server-side through Vaadin components; do not switch assistant rendering to raw HTML unless sanitization and link handling are deliberately redesigned.
- Keep configuration in `application.properties` and inject runtime config with MicroProfile `@ConfigProperty` where Java code needs it.
- Do not hardcode secrets. `PersonalMCPServer` expects `github.token` from `GITHUB_TOKEN` or runtime config.

### Framework-Specific Rules

- Treat `CustomerSupportAgent` as the central AI contract. Preserve `@RegisterAiService`, `@InputGuardrails(MentoringScopeGuardrail.class)`, `@McpToolBox("menthoring")`, and the `Multi<String> chat(String userMessage)` streaming signature unless the full UI stream path is updated too.
- Preserve the system-message contract: technical questions must check the `menthoring` MCP tool first, final user-facing answers must be Markdown, and the exact out-of-scope sentinel response must remain `"sorry, can not answer questions out of my scope"`.
- Keep `MentoringScopeGuardrail` aligned with `CustomerSupportAgent`: it rewrites unsupported input to the exact sentinel `[[OUT_OF_SCOPE]]`; changing either side requires changing both.
- Keep the MCP client name `menthoring` in `application.properties` aligned with `@McpToolBox("menthoring")` and the sibling `PersonalMCPServer` tool name.
- `PersonalMCPServer` MCP calls to `menthoring` expect JSON-RPC `tools/call` arguments shaped as `{ "keyWords": ["..."] }`; do not use the old `session` field.
- `PersonalMCPServer` normalizes and caps `keyWords` to five entries and searches `bevilacqua1996/Menthoring-Documentation`; prompt or client changes should account for keyword-based retrieval, not free-form long queries.
- Keep Vaadin push enabled for streaming responses with `attachEvent.getUI().getPushConfiguration().setPushMode(PushMode.AUTOMATIC)`.
- Do not edit generated Vaadin files under `src/main/frontend/generated`; build/runtime changes should be made in Java components, resources, Maven config, or Vaadin-supported frontend entry points.
- RAG ingestion currently clears the embedding store on startup with `store.removeAll()` for demo freshness. Do not remove or productionize this behavior accidentally; make any persistence change explicit.
- RAG retrieval intentionally uses `maxResults(1)` to keep context small and local-model calls fast.
- The custom RAG `ContentInjector` tells the model to only use retrieved information. Preserve that constraint unless changing the assistant's grounding policy.

### Testing Rules

- `quarkus-chat-poc` currently has no committed test source tree. When adding behavior, prefer focused Quarkus/JUnit tests for backend contracts and keep UI stream behavior covered at the smallest practical level.
- Use `./mvnw package` as the baseline verification command for `quarkus-chat-poc`; use `./mvnw quarkus:dev` for manual local chat validation.
- For guardrail changes, test both allowed technical/small-talk inputs and unsupported inputs rewritten to `[[OUT_OF_SCOPE]]`.
- For `CustomerSupportAgent` contract changes, verify the annotations and streaming signature still line up with the Vaadin UI subscription path.
- For RAG changes, test ingestion/retrieval assumptions: document location, splitter size, embedding dimension `384`, and `maxResults(1)` behavior when relevant.
- For MCP integration changes, also run or update tests in `../PersonalMCPServer` with `./gradlew test`.
- Preserve the sibling MCP server tests that assert `initialize`, `tools/list`, and `tools/call` behavior, especially the `menthoring` call with `arguments.keyWords`.
- Do not require a live GitHub token for ordinary tests. Existing `PersonalMCPServer` tests expect useful fallback/reminder responses when the token is missing.

### Code Quality & Style Rules

- Keep project code under `com.jcon` for `quarkus-chat-poc`; backend AI/RAG/guardrail code belongs in `com.jcon.backend`, Vaadin views in `com.jcon.ui.views`, and reusable UI pieces in `com.jcon.ui.components`.
- Keep `PersonalMCPServer` code under `com.bevilacqua1996.mcpServerPersonal`; do not mix its package namespace into this app.
- Use PascalCase Java class names and keep component class names aligned with their purpose, such as `ChatBubble`, `ChatTranscript`, or `MainViewContent`.
- Keep component CSS in `src/main/resources/META-INF/resources/style-components/*.css` when it belongs to a component, loaded with that component's `@StyleSheet`.
- Keep app-wide CSS in `src/main/resources/META-INF/resources/styles.css`; avoid scattering global styles into component files.
- Use stable CSS class names with the existing `chat-*` / `chat-*-__*` naming style.
- Keep markdown rendering conservative: headings, lists, paragraphs, code blocks, inline emphasis/code, and HTTP links are supported. Do not assume full CommonMark support without extending `MarkdownRenderer`.
- Prefer explicit, readable Java over clever abstractions; this is a small PoC and should stay easy to inspect.
- Keep comments sparse and useful. Existing comments explain non-obvious demo tradeoffs, such as clearing the embedding store and limiting RAG results.
- Avoid editing `target/`, `node_modules/`, `build/`, `bin/`, or generated Vaadin frontend files.

### Development Workflow Rules

- Work from `quarkus-chat-poc` when changing the chat application; use the Maven wrapper there.
- Work from `../PersonalMCPServer` when changing MCP server behavior; use the Gradle wrapper there.
- To manually validate the full integration, start `PersonalMCPServer` with `./gradlew quarkusDev` so `http://localhost:8081/api/mcp` is available before starting `quarkus-chat-poc` with `./mvnw quarkus:dev`.
- Keep `GITHUB_TOKEN`/`github.token` configuration outside committed source changes. The placeholder `TOKEN` means unauthenticated/fallback behavior.
- When changing MCP tool names, request schemas, port, or endpoint paths, update both repositories together: `application.properties`, `CustomerSupportAgent`, and `PersonalMCPServer` tests/docs.
- When changing the knowledge policy, update both the local RAG file in `src/main/resources/rag` and any MCP documentation assumptions if they overlap.
- Do not commit local build outputs or generated dependency directories from either repo.
- There is no documented branch, commit, or PR naming convention in this repo; do not invent one in code or docs unless the user requests it.

### Critical Don't-Miss Rules

- Do not break the exact guardrail handshake: unsupported input becomes `[[OUT_OF_SCOPE]]`, and the agent must answer exactly `sorry, can not answer questions out of my scope`.
- Do not let technical answers bypass MCP. The system prompt intentionally requires `menthoring` first for technical questions and for the listed keywords.
- Do not change `@McpToolBox("menthoring")` or `quarkus.langchain4j.mcp.menthoring.*` without updating the sibling MCP server/client contract.
- Do not send `session` to the MCP `menthoring` tool. The current contract is `keyWords: string[]`.
- Do not assume `PersonalMCPServer` returns JSON for `menthoring`; it returns text excerpts wrapped in MCP content.
- Do not remove `UI.access(...)` around streaming UI updates; Vaadin UI mutation from async callbacks must be marshaled to the UI thread.
- Do not allow multiple simultaneous chat responses unless the concurrency model is redesigned. `responseInFlight` prevents overlapping streams.
- Do not replace server-side Markdown component rendering with unsanitized HTML.
- Do not silently increase RAG context size or retrieval count; local Ollama latency and prompt size are part of the demo constraints.
- Do not treat generated Vaadin frontend files, `target/`, `node_modules/`, or sibling `build/` output as source of truth.
- Do not introduce a live GitHub token requirement into normal local tests or builds.

---

## Usage Guidelines

**For AI Agents:**

- Read this file before implementing any code.
- Follow all rules exactly as documented.
- When in doubt, prefer the more restrictive option.
- Update this file if new project-specific patterns emerge.

**For Humans:**

- Keep this file lean and focused on agent needs.
- Update it when technology stack, MCP contracts, or RAG behavior changes.
- Review periodically for outdated rules.
- Remove rules that become obvious over time.

Last Updated: 2026-06-10
