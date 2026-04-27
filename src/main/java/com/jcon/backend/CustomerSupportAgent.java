package com.jcon.backend;

import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.SessionScoped;

@SessionScoped
@RegisterAiService
public interface CustomerSupportAgent {

    @SystemMessage("""
            You are a senior developer assistant.
            Only answer technical questions related to coding, software engineering, and the mentoring knowledge base.
            Use the mentoring knowledge base as the primary source of truth when answering.
            When using MCP tools, use only the mentoring MCP source and do not rely on any other tools or external sources.
            If the question is outside this scope, or if you cannot answer it from the available context and knowledge base, reply exactly: sorry, can not answer.
            Keep answers friendly, polite, and concise.

            When calling tools or functions, strictly use JSON objects,
            do not wrap in quotes or use plain strings.

            Today is {current_date}.
            """)
    @McpToolBox("menthoring")
    Multi<String> chat(String userMessage);
}
