package com.github.paulakimenko.fakeses.dao;

import com.github.paulakimenko.fakeses.models.Action;
import com.github.paulakimenko.fakeses.models.Message;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessagesFileDAOTest extends BaseFileDAOTest {
    private static final String NEW_MESSAGE_JSON =
            "{\n  " +
                    "\"id\": \"fe601353-89c0-425f-bb34-5b3181c14136\",\n  " +
                    "\"action\": \"SendRawEmail\",\n  " +
                    "\"subject\": \"Hello new email tester!\",\n  " +
                    "\"source\": \"sender@example.com\",\n  " +
                    "\"destination\": [\n    \"example@gmail.com\"\n  ],\n  " +
                    "\"reply_to_addresses\": [\n    " +
                    "\"sender@example.com\"\n  ],\n  " +
                    "\"date_received\": 1480359900\n" +
                    "}";

    private static final Message NEW_MESSAGE_OBJ = new Message()
            .setId(UUID.fromString("fe601353-89c0-425f-bb34-5b3181c14136"))
            .setAction(Action.SEND_RAW_EMAIL)
            .setSubject("Hello new email tester!")
            .setDateReceived(1480359900)
            .setSource("sender@example.com")
            .setDestination(singletonList("example@gmail.com"))
            .setReplyToAddresses(singletonList("sender@example.com"))
            .setRawContent(RAW_MESSAGE)
            .setTextContent("Hello, email tester!\n")
            .setHtmlContent("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Hello</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    Hello, email tester!\n" +
                    "</body>\n" +
                    "</html>\n");

    private static final Message RAW_MESSAGE_OBJ = new Message()
            .setId(UUID.fromString("476a2602-7993-4c67-90b4-65d81d0cbd4b"))
            .setAction(Action.SEND_RAW_EMAIL)
            .setSubject("Hello email tester!")
            .setDateReceived(1480349900)
            .setSource("sender@example.com")
            .setDestination(singletonList("example@gmail.com"))
            .setReplyToAddresses(singletonList("sender@example.com"))
            .setRawContent(RAW_MESSAGE)
            .setTextContent("Hello, email tester!\n")
            .setHtmlContent("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Hello</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    Hello, email tester!\n" +
                    "</body>\n" +
                    "</html>\n");

    private static final Message MESSAGE_OBJ = new Message()
            .setId(UUID.fromString("85e17c36-080e-42b7-8cf5-d98cfd9a93b7"))
            .setAction(Action.SEND_EMAIL)
            .setSubject("Hello email tester 2!")
            .setDateReceived(1480350000)
            .setSource("sender@example.com")
            .setDestination(singletonList("example@gmail.com"))
            .setReplyToAddresses(singletonList("sender@example.com"))
            .setTextContent("Hello, email tester!\n")
            .setHtmlContent("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Hello</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    Hello, email tester!\n" +
                    "</body>\n" +
                    "</html>\n");

    private static final Message MIN_MESSAGE_OBJ = new Message()
            .setId(UUID.fromString("a9583fd2-eee1-4ca4-9c95-7b0f6784d985"))
            .setAction(Action.SEND_EMAIL)
            .setSubject("Hello email tester 3!")
            .setDateReceived(1480349800)
            .setSource("sender@example.com")
            .setDestination(singletonList("example@gmail.com"))
            .setReplyToAddresses(singletonList("sender@example.com"))
            .setTextContent("Hello, email tester!\n");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void itShouldCreateMessage() {
        Path messageDir = Paths.get(NEW_MESSAGE_OBJ.getId().toString());

        testDao.createMessage(NEW_MESSAGE_OBJ);

        assertFileExists(messageDir, true);
        assertFileContent(messageDir.resolve("messageinfo.json"), equalTo(NEW_MESSAGE_JSON));
        assertFileContent(messageDir.resolve("content.txt"), equalTo(NEW_MESSAGE_OBJ.getTextContent()));
        assertFileContent(messageDir.resolve("content.html"), equalTo(NEW_MESSAGE_OBJ.getHtmlContent()));
        assertFileContent(messageDir.resolve("raw.txt"), equalTo(NEW_MESSAGE_OBJ.getRawContent()));
    }

    @Test
    public void itShouldReturnErrorWhenMessageNullGiven() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(equalTo("Message is null!"));

        testDao.createMessage(null);
    }

    @Test
    public void itShouldReturnErrorWhenMessageWithoutIdGiven() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(equalTo("Message id is null!"));

        testDao.createMessage(new Message());
    }

    @Test
    public void itShouldGetRawMessageById() {
        testGetMessageById(RAW_MESSAGE_OBJ);
    }

    @Test
    public void itShouldGetMessageById() {
        testGetMessageById(MESSAGE_OBJ);
    }

    @Test
    public void itShouldGetMinMessageById() {
        testGetMessageById(MIN_MESSAGE_OBJ);
    }

    @Test
    public void itShouldGetAllMessages() {
        List<Message> messages = testDao.getMessages(message -> true);

        assertThat(messages, hasItems(MIN_MESSAGE_OBJ, RAW_MESSAGE_OBJ, MESSAGE_OBJ));
    }

    @Test
    public void itShouldGetMessagesByGivenSearchCriteria() {
        List<Message> messages = testDao.getMessages(message -> "Hello email tester!".equals(message.getSubject()));

        assertThat(messages, hasItem(RAW_MESSAGE_OBJ));
        assertThat(messages, not(hasItems(MIN_MESSAGE_OBJ, MESSAGE_OBJ)));
    }

    @Test
    public void itShouldZDeleteAllMessages() {
        testDao.deleteAllMessages();

        assertTestDirIsEmpty();
    }

    private static void testGetMessageById(Message testMessage) {
        Optional<Message> optionalMessage = testDao.getMessageById(testMessage.getId());
        assertTrue(optionalMessage.isPresent());

        Message message = optionalMessage.get();
        assertThat(message, equalTo(testMessage));
    }
}
