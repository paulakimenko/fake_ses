package com.github.paulakimenko.fakeses.utils;

import com.github.paulakimenko.fakeses.BaseSESMockTest;
import com.github.paulakimenko.fakeses.models.Action;
import com.github.paulakimenko.fakeses.models.Message;
import org.junit.Test;

import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class RawMessageParserTest extends BaseSESMockTest {
    @Test
    public void itShouldParseCorrectRawMessage() {
        UUID id = UUID.randomUUID();
        Action action = Action.SEND_RAW_EMAIL;
        long dateReceived = 1480349900;

        Message parsedMessage = RawMessageParser.parse(id, action, dateReceived, RAW_MESSAGE);

        assertThat(parsedMessage.getId(), equalTo(id));
        assertThat(parsedMessage.getAction(), equalTo(action));
        assertThat(parsedMessage.getDateReceived(), equalTo(dateReceived));
        assertThat(parsedMessage.getSubject(), equalTo("Hello email tester!"));
        assertThat(parsedMessage.getSource(), equalTo("sender@example.com"));
        assertThat(parsedMessage.getDestination(), equalTo(singletonList("example@gmail.com")));
        assertThat(parsedMessage.getReplyToAddresses(), equalTo(singletonList("sender@example.com")));
        assertThat(parsedMessage.getRawContent(), equalTo(RAW_MESSAGE));
        assertThat(parsedMessage.getTextContent(), equalTo("Hello, email tester!\n"));
        assertThat(parsedMessage.getHtmlContent(), equalTo(
                "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Hello</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    Hello, email tester!\n" +
                        "</body>\n" +
                        "</html>\n"
        ));
    }
}
