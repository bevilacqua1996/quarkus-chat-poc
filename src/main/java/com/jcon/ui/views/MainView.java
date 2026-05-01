package com.jcon.ui.views;

import java.util.concurrent.atomic.AtomicBoolean;

import com.jcon.backend.CustomerSupportAgent;
import com.jcon.ui.components.ChatBubble;
import com.jcon.ui.components.MainViewContent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.shared.communication.PushMode;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Code Mentoring Knowledge Base")
public class MainView extends VerticalLayout {

    private final CustomerSupportAgent customerSupportAgent;
    private final MainViewContent content = new MainViewContent();
    private final AtomicBoolean responseInFlight = new AtomicBoolean(false);
    private boolean introShown = false;

    public MainView(CustomerSupportAgent customerSupportAgent) {
        this.customerSupportAgent = customerSupportAgent;
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        add(content);

        content.getActionBar().getSendButton().addClickListener(event -> sendCurrentMessage());
        content.getActionBar().getClearButton().addClickListener(event -> {
            if (responseInFlight.get()) {
                Notification.show("Wait for the current response to finish before clearing.", 2500, Notification.Position.TOP_CENTER);
                return;
            }
            content.resetConversation();
            content.getConnectionStatus().setStatus("Ready");
        });

        DomListenerRegistration enterShortcut = content.getMessageInput().getElement().addEventListener("keydown", event -> {
            sendCurrentMessage();
        });
        enterShortcut.setFilter("event.key === 'Enter' && !event.shiftKey");
        enterShortcut.preventDefault();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        attachEvent.getUI().getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
        showIntroductionIfNeeded();
        content.getConnectionStatus().setStatus("Ready");
    }

    private void showIntroductionIfNeeded() {
        if (introShown) {
            return;
        }

        if (content.getTranscript().getComponentCount() == 0) {
            content.getTranscript().appendMessage("system", "Hello, how can I help you with your coding today? :)");
        }
        introShown = true;
    }

    private void sendCurrentMessage() {
        if (!responseInFlight.compareAndSet(false, true)) {
            Notification.show("Please wait for the current response to finish.", 2500, Notification.Position.TOP_CENTER);
            return;
        }

        String message = content.getMessageInput().getValue() == null ? "" : content.getMessageInput().getValue().trim();
        if (message.isEmpty()) {
            responseInFlight.set(false);
            Notification.show("Please enter a message first.", 2000, Notification.Position.TOP_CENTER);
            return;
        }

        UI ui = getUI().orElse(null);
        if (ui == null) {
            responseInFlight.set(false);
            return;
        }

        content.getMessageInput().clear();
        setChatEnabled(false);
        content.getConnectionStatus().setStatus("Thinking...");

        content.getTranscript().appendMessage("user", message);
        ChatBubble assistantBubble = content.getTranscript().appendMessage("assistant", " ");
        StringBuilder assistantBuffer = new StringBuilder();

        try {
            customerSupportAgent.chat(message).subscribe().with(
                chunk -> ui.access(() -> {
                    assistantBuffer.append(chunk);
                    assistantBubble.setMarkdown(assistantBuffer.toString());
                }),
                failure -> ui.access(() -> {
                    responseInFlight.set(false);
                    setChatEnabled(true);
                    content.getConnectionStatus().setStatus("Ready");
                    assistantBubble.setMarkdown("Sorry, something went wrong while generating the answer.");
                    Notification.show("Failed to generate a response.", 2500, Notification.Position.TOP_CENTER);
                }),
                () -> ui.access(() -> {
                    responseInFlight.set(false);
                    setChatEnabled(true);
                    content.getConnectionStatus().setStatus("Ready");
                })
            );
        } catch (RuntimeException exception) {
            responseInFlight.set(false);
            setChatEnabled(true);
            content.getConnectionStatus().setStatus("Ready");
            assistantBubble.setMarkdown("Sorry, something went wrong while generating the answer.");
            Notification.show("Failed to start the response stream.", 2500, Notification.Position.TOP_CENTER);
        }
    }

    private void setChatEnabled(boolean enabled) {
        content.getMessageInput().setEnabled(enabled);
        content.getActionBar().getSendButton().setEnabled(enabled);
    }
}
