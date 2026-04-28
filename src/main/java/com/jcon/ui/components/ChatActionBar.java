package com.jcon.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ChatActionBar extends HorizontalLayout {

    private final Button sendButton;
    private final Button clearButton;

    public ChatActionBar() {
        sendButton = new Button("Send", new Icon(VaadinIcon.PAPERPLANE));
        clearButton = new Button("Clear", new Icon(VaadinIcon.TRASH));

        sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        setAlignItems(FlexComponent.Alignment.END);
        setWidthFull();
        add(sendButton, clearButton);
    }

    public Button getSendButton() {
        return sendButton;
    }

    public Button getClearButton() {
        return clearButton;
    }
}
