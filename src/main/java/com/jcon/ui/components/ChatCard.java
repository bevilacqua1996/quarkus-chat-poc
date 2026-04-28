package com.jcon.ui.components;

import com.vaadin.flow.component.html.Div;

public class ChatCard extends Div {

    public ChatCard(ChatTranscript transcript, ChatMessageInput messageInput, ChatActionBar actionBar) {
        add(transcript, messageInput, actionBar);
        getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "var(--lumo-space-m)")
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("box-shadow", "var(--lumo-box-shadow-m)");
    }
}
