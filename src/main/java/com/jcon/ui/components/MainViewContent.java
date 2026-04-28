package com.jcon.ui.components;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@StyleSheet("style-components/main-view-content.css")
public class MainViewContent extends VerticalLayout {

    private final ChatConnectionStatus connectionStatus;
    private final ChatTranscript transcript;
    private final ChatMessageInput messageInput;
    private final ChatActionBar actionBar;

    public MainViewContent() {
        setWidthFull();
        setHeightFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(FlexComponent.Alignment.STRETCH);
        addClassName("main-view-content");

        var title = new ViewTitle("Code Mentoring Knowledge Base");
        title.addClassName("title");

        var description = new Paragraph("Ask the support agent about the codebase and watch the streamed response appear below.");
        description.addClassName("description");

        connectionStatus = new ChatConnectionStatus();
        Div statusRow = new Div(connectionStatus);
        statusRow.addClassName("status-row");

        transcript = new ChatTranscript();
        messageInput = new ChatMessageInput();
        actionBar = new ChatActionBar();

        ChatCard chatCard = new ChatCard(transcript, messageInput, actionBar);

        Div shell = new Div(title, description, statusRow, chatCard);
        shell.addClassName("shell");

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
