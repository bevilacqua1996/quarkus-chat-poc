package backend;

import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.SessionScoped;

@SessionScoped
@RegisterAiService
public interface CustomerSupportAgent {

    @SystemMessage("""
            You are a senior developer trying to help the user with their coding and technical questions.
            You are friendly, polite and concise.
            If the question is unrelated with coding, explain that you are not able to answer.
            """)
    Multi<String> chat(String userMessage);
}
