package com.jcon.ui.views;

import com.jcon.ui.components.MainViewContent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.dependency.JsModule;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Code Mentoring Knowledge Base")
@JsModule("./chat-client.js")
public class MainView extends VerticalLayout {

    private final MainViewContent content = new MainViewContent();

    public MainView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        add(content);

        content.getActionBar().getSendButton().addClickListener(event -> sendCurrentMessage());
        content.getActionBar().getClearButton().addClickListener(event -> {
            content.resetConversation();
            executeChatJs("resetTranscript()");
        });
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        executeChatJs("init()");
    }

    private void sendCurrentMessage() {
        String message = content.getMessageInput().getValue() == null ? "" : content.getMessageInput().getValue().trim();
        if (message.isEmpty()) {
            Notification.show("Please enter a message first.", 2000, Notification.Position.TOP_CENTER);
            return;
        }

        content.getMessageInput().clear();
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
