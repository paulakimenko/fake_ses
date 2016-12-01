package com.github.paulakimenko.fakeses.utils;

import com.github.paulakimenko.fakeses.models.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public final class MessageFilter {
    private static final Map<String, PredicateProvider> FILTERS_MAP = new HashMap<>();

    static {
        FILTERS_MAP.put(
                "subject",
                filterValue ->
                        message ->
                                message.getSubject().equalsIgnoreCase(filterValue.get())
        );

        FILTERS_MAP.put(
                "source",
                filterValue ->
                        message ->
                                message.getSource().equalsIgnoreCase(filterValue.get())
        );

        FILTERS_MAP.put(
                "destination",
                filterValue ->
                        message ->
                                message.getDestination().stream().map(String::toLowerCase).collect(toList())
                                        .containsAll(Stream.of(filterValue.get().split(","))
                                                .map(String::toLowerCase).collect(toList()))
        );

        FILTERS_MAP.put(
                "received_after",
                filterValue ->
                        message ->
                                message.getDateReceived() >= parseLong(filterValue.get())
        );

        FILTERS_MAP.put(
                "received_before",
                filterValue ->
                        message ->
                                message.getDateReceived() < parseLong(filterValue.get())
        );

        FILTERS_MAP.put(
                "text_contains",
                filterValue ->
                        message ->
                                message.getTextContent().toLowerCase().contains(filterValue.get().toLowerCase())
        );

        FILTERS_MAP.put(
                "html_contains",
                filterValue ->
                        message ->
                                message.getHtmlContent().toLowerCase().contains(filterValue.get().toLowerCase())
        );
    }

    private MessageFilter() {}

    private interface PredicateProvider extends Function<Supplier<String>, Predicate<Message>> {}
    private static final Predicate<Message> TRUE = message -> true;

    public static Predicate<Message> fromArgsMap(Map<String, String> filterArgs) {
        return filterArgs
                .entrySet()
                .stream()
                .map(filterArgEntry -> FILTERS_MAP.containsKey(filterArgEntry.getKey())
                        ? FILTERS_MAP.get(filterArgEntry.getKey()).apply(filterArgEntry::getValue)
                        : TRUE)
                .reduce(Predicate::and)
                .orElse(TRUE);
    }

    private static long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
