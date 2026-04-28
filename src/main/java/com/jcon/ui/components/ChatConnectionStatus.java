package com.jcon.ui.components;

import com.vaadin.flow.component.html.Span;

public class ChatConnectionStatus extends Span {

    public static final String ELEMENT_ID = "connection-status";

    public ChatConnectionStatus() {
        super("Connecting...");
        getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "0.5rem")
                .set("font-weight", "600")
                .set("color", "var(--lumo-secondary-text-color)");
        setId(ELEMENT_ID);
        getElement().setAttribute("aria-live", "polite");
    }
}
