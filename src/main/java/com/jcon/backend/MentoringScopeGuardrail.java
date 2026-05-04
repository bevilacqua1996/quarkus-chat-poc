package com.jcon.backend;

import java.util.Locale;
import java.util.Set;

import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailRequest;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MentoringScopeGuardrail implements InputGuardrail {

    private static final String OUT_OF_SCOPE_SENTINEL = "[[OUT_OF_SCOPE]]";

    private static final Set<String> TECH_KEYWORDS = Set.of(
        "java", "quarkus", "rest", "REST", "api", "API", "rest api", "REST API", "docker",
        "jvm", "JVM", "session", "training", "menthoring", "code", "coding", "software", "engineering",
        "debug", "bug", "error", "exception", "stack trace", "mcp", "rag",
        "llm", "prompt", "markdown", "json", "database", "sql", "http", "websocket",
        "ui", "vaadin", "frontend", "backend", "maven"
    );

    @Override
    public InputGuardrailResult validate(InputGuardrailRequest request) {
        String text = request.userMessage() == null ? "" : request.userMessage().singleText();
        String normalized = text.toLowerCase(Locale.ROOT);

        if (isCasualSmallTalk(normalized) || isTechnical(normalized)) {
            return success();
        }

        return successWith(OUT_OF_SCOPE_SENTINEL);
    }

    private boolean isTechnical(String message) {
        for (String keyword : TECH_KEYWORDS) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCasualSmallTalk(String message) {
        return message.contains("how are you")
            || message.contains("how're you")
            || message.contains("how are u")
            || message.contains("what's up")
            || message.contains("whats up")
            || message.contains("are you ok")
            || message.contains("are you okay")
            || message.contains("hello")
            || message.contains("hi")
            || message.contains("hey");
    }
}
