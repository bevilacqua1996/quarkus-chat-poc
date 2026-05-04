package com.jcon.ui.components;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;

@StyleSheet("context://style-components/chat-message-input.css")
public class ChatMessageInput extends TextArea {

    public static final String ELEMENT_ID = "chat-message-input";

    public ChatMessageInput() {
        super("Message");
        setWidthFull();
        setPlaceholder("Type a message and press Send");
        setId(ELEMENT_ID);
        setValueChangeMode(ValueChangeMode.EAGER);
        addClassName("chat-message-input");
    }
}
