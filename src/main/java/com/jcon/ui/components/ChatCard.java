package com.jcon.ui.components;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;

@StyleSheet("style-components/chat-card.css")
public class ChatCard extends Div {

    public ChatCard(ChatTranscript transcript, ChatMessageInput messageInput, ChatActionBar actionBar) {
        add(transcript, messageInput, actionBar);
        addClassName("chat-card");
    }
}
