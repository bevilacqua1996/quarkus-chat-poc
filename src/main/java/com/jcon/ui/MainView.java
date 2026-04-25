package com.jcon.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.dependency.JsModule;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Code Mentoring Knowledge Base")
@JsModule("./chat-client.js")
public class MainView extends VerticalLayout {

    private final Span connectionStatus = new Span("Connecting...");
    private final TextArea transcript = new TextArea();
    private final TextArea messageInput = new TextArea("Message");
    private final Button sendButton = new Button("Send", new Icon(VaadinIcon.PAPERPLANE));
    private final Button clearButton = new Button("Clear", new Icon(VaadinIcon.TRASH));

    public MainView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(FlexComponent.Alignment.STRETCH);
        addClassName(LumoUtility.Padding.MEDIUM);

        ViewTitle title = new ViewTitle("Code Mentoring Knowledge Base");
        title.addClassName(LumoUtility.Margin.NONE);

        Paragraph description = new Paragraph("Ask the support agent about the codebase and watch the streamed response appear below.");
        description.addClassName(LumoUtility.TextColor.SECONDARY);

        connectionStatus.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "0.5rem")
                .set("font-weight", "600");
        connectionStatus.getStyle().set("color", "var(--lumo-secondary-text-color)");
        connectionStatus.setId("connection-status");
        connectionStatus.getElement().setAttribute("aria-live", "polite");

        Div statusRow = new Div(connectionStatus);
        statusRow.getStyle().set("margin-bottom", "var(--lumo-space-m)");

        transcript.setWidthFull();
        transcript.setHeightFull();
        transcript.setReadOnly(true);
        transcript.setId("chat-transcript");
        transcript.setValue("Responses will appear here.\n");
        transcript.getStyle()
                .set("font-family", "var(--lumo-font-family)")
                .set("white-space", "pre-wrap")
                .set("flex", "1 1 auto")
                .set("min-height", "16rem");

        messageInput.setWidthFull();
        messageInput.getStyle()
                .set("min-height", "7rem")
                .set("max-height", "12rem");
        messageInput.setId("chat-message-input");
        messageInput.setPlaceholder("Type a message and press Send");

        sendButton.addClickListener(event -> sendCurrentMessage());
        clearButton.addClickListener(event -> {
            transcript.setValue("Responses will appear here.\n");
            messageInput.clear();
            executeChatJs("resetTranscript()");
        });

        sendButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY);
        clearButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(sendButton, clearButton);
        actions.setAlignItems(FlexComponent.Alignment.END);
        actions.setWidthFull();

        Div chatCard = new Div(transcript, messageInput, actions);
        chatCard.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "var(--lumo-space-m)")
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("box-shadow", "var(--lumo-box-shadow-m)")
                .set("flex", "1")
                .set("min-height", "0");

        Div shell = new Div(title, description, statusRow, chatCard);
        shell.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "var(--lumo-space-m)")
                .set("width", "min(100%, 980px)")
                .set("margin", "0 auto")
                .set("flex", "1")
                .set("min-height", "0");

        add(shell);
        expand(shell);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        executeChatJs("init()");
    }

    private void sendCurrentMessage() {
        String message = messageInput.getValue() == null ? "" : messageInput.getValue().trim();
        if (message.isEmpty()) {
            Notification.show("Please enter a message first.", 2000, Notification.Position.TOP_CENTER);
            return;
        }

        transcript.setValue(transcript.getValue() + "\nYou: " + message + "\nAssistant: ");
        messageInput.clear();
        executeChatJs("sendMessage($0)", message);
    }

    private void executeChatJs(String expression, Object... arguments) {
        UI ui = getUI().orElse(null);
        if (ui == null) {
            return;
        }
        Page page = ui.getPage();
        page.executeJs("window.customerSupportChat && window.customerSupportChat." + expression, arguments);
    }
}
