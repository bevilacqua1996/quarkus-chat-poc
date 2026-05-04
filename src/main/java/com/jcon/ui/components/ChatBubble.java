package com.jcon.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;

@StyleSheet("context://style-components/chat-bubbles.css")
public class ChatBubble extends Div {

    private final Div content = new Div();

    public ChatBubble(String role, String markdown) {
        addClassName("chat-bubble");
        addClassName("chat-bubble--" + role);
        setWidthFull();

        content.addClassName("chat-bubble__content");
        content.setWidthFull();
        add(content);

        setMarkdown(markdown);
    }

    public void setMarkdown(String markdown) {
        content.removeAll();
        for (Component component : MarkdownRenderer.render(markdown)) {
            content.add(component);
        }
    }
}
