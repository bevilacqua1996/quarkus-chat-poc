package com.jcon.ui.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainViewContent extends VerticalLayout {

    private final ChatConnectionStatus connectionStatus;
    private final ChatTranscript transcript;
    private final ChatMessageInput messageInput;
    private final ChatActionBar actionBar;

    public MainViewContent() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(FlexComponent.Alignment.STRETCH);
        addClassName(LumoUtility.Padding.MEDIUM);

        var title = new ViewTitle("Code Mentoring Knowledge Base");
        title.addClassName(LumoUtility.Margin.NONE);

        var description = new Paragraph("Ask the support agent about the codebase and watch the streamed response appear below.");
        description.addClassName(LumoUtility.TextColor.SECONDARY);

        connectionStatus = new ChatConnectionStatus();
        Div statusRow = new Div(connectionStatus);
        statusRow.getStyle().set("margin-bottom", "var(--lumo-space-m)");

        transcript = new ChatTranscript();
        messageInput = new ChatMessageInput();
        actionBar = new ChatActionBar();

        ChatCard chatCard = new ChatCard(transcript, messageInput, actionBar);

        Div shell = new Div(title, description, statusRow, chatCard);
        shell.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "var(--lumo-space-m)")
                .set("width", "min(100%, 980px)")
                .set("margin", "0 auto");

        add(shell);
    }

    public ChatConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public ChatTranscript getTranscript() {
        return transcript;
    }

    public ChatMessageInput getMessageInput() {
        return messageInput;
    }

    public ChatActionBar getActionBar() {
        return actionBar;
    }

    public void resetConversation() {
        transcript.reset();
        messageInput.clear();
    }
}
