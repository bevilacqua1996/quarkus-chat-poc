package com.jcon.ui.components;

import com.vaadin.flow.component.textfield.TextArea;

public class ChatMessageInput extends TextArea {

    public static final String ELEMENT_ID = "chat-message-input";

    public ChatMessageInput() {
        super("Message");
        setWidthFull();
        setPlaceholder("Type a message and press Send");
        setId(ELEMENT_ID);
        getStyle()
                .set("min-height", "7rem")
                .set("max-height", "12rem");
    }
}
