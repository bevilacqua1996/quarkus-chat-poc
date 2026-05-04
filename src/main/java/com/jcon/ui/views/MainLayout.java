package com.jcon.ui.views;

import com.jcon.ui.components.ViewTitle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.Layout;

@Layout
@StyleSheet("frontend/chat-bubbles.css")
public final class MainLayout extends AppLayout {

    public MainLayout() {
        setPrimarySection(Section.NAVBAR);
        // place header in the navbar so its DrawerToggle remains visible
        addToNavbar(createApplicationHeader());
    }

    private Component createApplicationHeader() {
    
        var appLogo = new Avatar("Knowledge Base");
        appLogo.addClassName("app-logo");
        appLogo.addThemeVariants(AvatarVariant.AURA_FILLED, AvatarVariant.XSMALL);

        var title = new ViewTitle("Knowledge Base");

        var header = new HorizontalLayout(appLogo, title);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(true);
        return header;
    }

}
