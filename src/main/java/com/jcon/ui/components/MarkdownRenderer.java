package com.jcon.ui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Code;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;

final class MarkdownRenderer {

    private static final Pattern HEADING_PATTERN = Pattern.compile("^(#{1,6})\\s+(.*)$");
    private static final Pattern ORDERED_LIST_PATTERN = Pattern.compile("^\\s*(\\d+)\\.\\s+(.*)$");
    private static final Pattern UNORDERED_LIST_PATTERN = Pattern.compile("^\\s*[-*]\\s+(.*)$");

    private MarkdownRenderer() {
    }

    static List<Component> render(String markdown) {
        List<Component> components = new ArrayList<>();
        if (markdown == null || markdown.isBlank()) {
            return components;
        }

        String[] lines = markdown.replace("\r\n", "\n").split("\n", -1);
        List<String> paragraphLines = new ArrayList<>();
        List<String> listItems = new ArrayList<>();
        Boolean orderedList = null;

        int index = 0;
        while (index < lines.length) {
            String line = lines[index];

            if (line.isBlank()) {
                flushParagraph(components, paragraphLines);
                orderedList = flushList(components, listItems, orderedList);
                index++;
                continue;
            }

            if (line.startsWith("```")) {
                flushParagraph(components, paragraphLines);
                orderedList = flushList(components, listItems, orderedList);

                StringBuilder code = new StringBuilder();
                index++;
                while (index < lines.length && !lines[index].startsWith("```")) {
                    if (!code.isEmpty()) {
                        code.append('\n');
                    }
                    code.append(lines[index]);
                    index++;
                }
                if (index < lines.length && lines[index].startsWith("```")) {
                    index++;
                }
                components.add(renderCodeBlock(code.toString()));
                continue;
            }

            Matcher headingMatcher = HEADING_PATTERN.matcher(line);
            if (headingMatcher.matches()) {
                flushParagraph(components, paragraphLines);
                orderedList = flushList(components, listItems, orderedList);
                components.add(renderHeading(headingMatcher.group(1).length(), headingMatcher.group(2)));
                index++;
                continue;
            }

            Matcher unorderedMatcher = UNORDERED_LIST_PATTERN.matcher(line);
            Matcher orderedMatcher = ORDERED_LIST_PATTERN.matcher(line);
            if (unorderedMatcher.matches() || orderedMatcher.matches()) {
                flushParagraph(components, paragraphLines);

                boolean currentOrderedList = orderedMatcher.matches();
                if (orderedList != null && orderedList.booleanValue() != currentOrderedList) {
                    orderedList = flushList(components, listItems, orderedList);
                }
                if (orderedList == null) {
                    orderedList = currentOrderedList;
                }

                String itemText = currentOrderedList ? orderedMatcher.group(2) : unorderedMatcher.group(1);
                listItems.add(itemText);
                index++;
                continue;
            }

            orderedList = flushList(components, listItems, orderedList);
            paragraphLines.add(line);
            index++;
        }

        flushParagraph(components, paragraphLines);
        flushList(components, listItems, orderedList);
        return components;
    }

    private static void flushParagraph(List<Component> components, List<String> paragraphLines) {
        if (paragraphLines.isEmpty()) {
            return;
        }

        Paragraph paragraph = new Paragraph();
        for (int i = 0; i < paragraphLines.size(); i++) {
            if (i > 0) {
                Span lineBreak = new Span();
                lineBreak.setText("\n");
                paragraph.add(lineBreak);
            }
            paragraph.add(renderInline(paragraphLines.get(i)));
        }
        components.add(paragraph);
        paragraphLines.clear();
    }

    private static Boolean flushList(List<Component> components, List<String> listItems, Boolean orderedList) {
        if (listItems.isEmpty() || orderedList == null) {
            listItems.clear();
            return null;
        }

        if (orderedList) {
            OrderedList list = new OrderedList();
            for (String item : listItems) {
                ListItem listItem = new ListItem();
                listItem.add(renderInline(item));
                list.add(listItem);
            }
            components.add(list);
        } else {
            UnorderedList list = new UnorderedList();
            for (String item : listItems) {
                ListItem listItem = new ListItem();
                listItem.add(renderInline(item));
                list.add(listItem);
            }
            components.add(list);
        }

        listItems.clear();
        return null;
    }

    private static Component renderHeading(int level, String text) {
        Component heading = switch (level) {
            case 1 -> new H1();
            case 2 -> new H2();
            case 3 -> new H3();
            case 4 -> new H4();
            case 5 -> new H5();
            default -> new H6();
        };
        ((HasComponents) heading).add(renderInline(text).toArray(Component[]::new));
        return heading;
    }

    private static Component renderCodeBlock(String code) {
        Pre pre = new Pre();
        pre.addClassName("chat-bubble__code-block");

        Code codeElement = new Code();
        codeElement.addClassName("chat-bubble__code");
        codeElement.setText(code);
        pre.add(codeElement);
        return pre;
    }

    private static List<Component> renderInline(String text) {
        List<Component> components = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return components;
        }

        StringBuilder buffer = new StringBuilder();
        int index = 0;
        while (index < text.length()) {
            if (text.startsWith("**", index)) {
                int end = text.indexOf("**", index + 2);
                if (end > index + 2) {
                    flushTextBuffer(components, buffer);
                    Span strong = new Span();
                    strong.getStyle().set("font-weight", "600");
                    strong.setText(text.substring(index + 2, end));
                    components.add(strong);
                    index = end + 2;
                    continue;
                }
            }

            if (text.charAt(index) == '*') {
                int end = text.indexOf('*', index + 1);
                if (end > index + 1) {
                    flushTextBuffer(components, buffer);
                    Span emphasis = new Span();
                    emphasis.getStyle().set("font-style", "italic");
                    emphasis.setText(text.substring(index + 1, end));
                    components.add(emphasis);
                    index = end + 1;
                    continue;
                }
            }

            if (text.charAt(index) == '`') {
                int end = text.indexOf('`', index + 1);
                if (end > index + 1) {
                    flushTextBuffer(components, buffer);
                    Code code = new Code();
                    code.addClassName("chat-bubble__inline-code");
                    code.setText(text.substring(index + 1, end));
                    components.add(code);
                    index = end + 1;
                    continue;
                }
            }

            if (text.charAt(index) == '[') {
                int labelEnd = text.indexOf(']', index + 1);
                int urlStart = labelEnd >= 0 && labelEnd + 1 < text.length() && text.charAt(labelEnd + 1) == '(' ? labelEnd + 2 : -1;
                int urlEnd = urlStart >= 0 ? text.indexOf(')', urlStart) : -1;
                if (labelEnd > index && urlStart > 0 && urlEnd > urlStart) {
                    String label = text.substring(index + 1, labelEnd);
                    String url = text.substring(urlStart, urlEnd);
                    if (isHttpUrl(url)) {
                        flushTextBuffer(components, buffer);
                        Anchor anchor = new Anchor(url, label);
                        anchor.setTarget("_blank");
                        anchor.getElement().setAttribute("rel", "noopener noreferrer");
                        components.add(anchor);
                        index = urlEnd + 1;
                        continue;
                    }
                }
            }

            buffer.append(text.charAt(index));
            index++;
        }

        flushTextBuffer(components, buffer);
        return components;
    }

    private static void flushTextBuffer(List<Component> components, StringBuilder buffer) {
        if (buffer.isEmpty()) {
            return;
        }
        Span span = new Span();
        span.setText(buffer.toString());
        components.add(span);
        buffer.setLength(0);
    }

    private static boolean isHttpUrl(String value) {
        return value.startsWith("http://") || value.startsWith("https://");
    }
}
