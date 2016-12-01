package com.github.paulakimenko.fakeses.utils;

import com.github.paulakimenko.fakeses.BaseSESMockTest;
import com.github.paulakimenko.fakeses.models.Message;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MessageFilterTest extends BaseSESMockTest {
    @Test
    public void itShouldFilterBySubject() {
        String expected = "DeadBeef";
        testFilter(new Message().setSubject(expected), new Message().setSubject("excluded"), "subject", expected);
    }

    @Test
    public void itShouldFilterBySource() {
        String expected = "DeadBeef@gmail.com";
        testFilter(new Message().setSubject(expected), new Message().setSubject("excluded@gmail.com"), "subject", expected);
    }

    @Test
    public void itShouldFilterByDestination() {
        List<String> expected = asList("DeadBeef@gmail.com", "some@yahoo.com");
        testFilter(
                new Message().setDestination(expected),
                new Message().setDestination(asList("qwe@gmail.com", "dsa@gmail.com")),
                "destination", expected.get(0)
        );
    }

    @Test
    public void itShouldFilterByReceivedAfter() {
        long expected = 1000000;
        testFilter(
                new Message().setDateReceived(expected + 1),
                new Message().setDateReceived(expected - 1),
                "received_after", valueOf(expected)
        );
    }

    @Test
    public void itShouldFilterByReceivedBefore() {
        long expected = 1000000;
        testFilter(
                new Message().setDateReceived(expected - 1),
                new Message().setDateReceived(expected + 1),
                "received_before", valueOf(expected)
        );
    }

    @Test
    public void itShouldFilterByTextContains() {
        String expected = "DeadBeef";
        testFilter(
                new Message().setTextContent("New" + expected + "Content"),
                new Message().setTextContent("excluded"),
                "text_contains", expected
        );
    }

    @Test
    public void itShouldFilterByHtmlContains() {
        String expected = "DeadBeef";
        testFilter(
                new Message().setHtmlContent("New" + expected + "Content"),
                new Message().setHtmlContent("excluded"),
                "html_contains", expected
        );
    }

    private static void testFilter(Message includedMessage, Message excludedMessage, String argName, String argValue) {
        Map<String, String> argsMap = new HashMap<>();
        argsMap.put(argName, argValue);

        Predicate<Message> messagePredicate = MessageFilter.fromArgsMap(argsMap);

        assertThat(messagePredicate.test(includedMessage), is(true));
        assertThat(messagePredicate.test(excludedMessage), is(false));
    }
}
