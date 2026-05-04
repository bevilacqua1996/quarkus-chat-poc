package com.jcon.ui.components;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Span;

@StyleSheet("style-components/chat-connection-status.css")
public class ChatConnectionStatus extends Span {

    public static final String ELEMENT_ID = "connection-status";

    public ChatConnectionStatus() {
        super("Ready");
        addClassName("chat-connection-status");
        setId(ELEMENT_ID);
        getElement().setAttribute("aria-live", "polite");
    }

    public void setStatus(String status) {
        setText(status);
    }
}
