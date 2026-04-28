package com.jcon.ui.components;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;

@StyleSheet("style-components/chat-transcript.css")
public class ChatTranscript extends Div {

    public static final String ELEMENT_ID = "chat-transcript";

    public ChatTranscript() {
        setWidthFull();
        setId(ELEMENT_ID);
        addClassName("chat-transcript");
    }

    public void reset() {
        removeAll();
    }

    public void setMarkdownContent(String markdown) {
        setText(markdown);
    }
}
