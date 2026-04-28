package com.jcon.ui.components;

import com.vaadin.flow.component.html.Div;

public class ChatTranscript extends Div {

    public static final String ELEMENT_ID = "chat-transcript";

    public ChatTranscript() {
        setWidthFull();
        setId(ELEMENT_ID);
        getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "var(--lumo-space-s)")
                .set("min-height", "10rem")
                .set("overflow-y", "auto")
                .set("padding", "var(--lumo-space-s)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("background", "var(--lumo-contrast-5pct)")
                .set("box-sizing", "border-box");
    }

    public void reset() {
        removeAll();
    }

    public void setMarkdownContent(String markdown) {
        setText(markdown);
    }
}
