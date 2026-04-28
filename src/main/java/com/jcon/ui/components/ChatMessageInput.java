package com.jcon.ui.components;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.textfield.TextArea;

@StyleSheet("style-components/chat-message-input.css")
public class ChatMessageInput extends TextArea {

    public static final String ELEMENT_ID = "chat-message-input";

    public ChatMessageInput() {
        super("Message");
        setWidthFull();
        setPlaceholder("Type a message and press Send");
        setId(ELEMENT_ID);
        addClassName("chat-message-input");
    }
}
