package com.github.paulakimenko.fakeses.controllers;

import com.github.paulakimenko.fakeses.models.Action;
import com.github.paulakimenko.fakeses.models.Message;
import com.jayway.restassured.RestAssured;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;

public class SESControllerTest extends BaseControllerTest {
    @Test
    public void itShouldPostMessage() {
        Message testMessage = generateTestMessage();

        RestAssured
                .given()
                        .param("Action", testMessage.getAction().getAwsField())
                        .param("Message.Subject.Data", testMessage.getSubject())
                        .param("Source", testMessage.getSource())
                        .param("Destination.ToAddresses.member.1", testMessage.getDestination().get(0))
                        .param("Destination.ToAddresses.member.2", testMessage.getDestination().get(1))
                        .param("ReplyToAddresses.member.1", testMessage.getReplyToAddresses().get(0))
                        .param("ReplyToAddresses.member.2", testMessage.getReplyToAddresses().get(1))
                        .param("Message.Body.Text.Data", testMessage.getTextContent())
                        .param("Message.Body.Html.Data", testMessage.getHtmlContent())
                .when()
                        .post("/")
                .then()
                        .statusCode(200)
                        .contentType("application/xml")
                        .body("SendEmailResponse.SendEmailResult.MessageId", is(not(equalTo(""))));

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(daoMock, times(1)).createMessage(messageCaptor.capture());
        Message savedMessage = messageCaptor.getValue();

        assertThat(savedMessage.getId(), notNullValue());
        assertThat(savedMessage.getDateReceived(), is(not(nullValue())));
        assertThat(savedMessage.getAction(), equalTo(testMessage.getAction()));
        assertThat(savedMessage.getSubject(), equalTo(testMessage.getSubject()));
        assertThat(savedMessage.getSource(), equalTo(testMessage.getSource()));
        assertThat(savedMessage.getDestination(), equalTo(testMessage.getDestination()));
        assertThat(savedMessage.getReplyToAddresses(), equalTo(testMessage.getReplyToAddresses()));
        assertThat(savedMessage.getTextContent(), equalTo(testMessage.getTextContent()));
        assertThat(savedMessage.getHtmlContent(), equalTo(testMessage.getHtmlContent()));
        assertThat(savedMessage.getRawContent(), is(nullValue()));
    }

    @Test
    public void itShouldPostRawMessage() {
        RestAssured
                .given()
                        .param("Action", Action.SEND_RAW_EMAIL.getAwsField())
                        .param("RawMessage.Data", RAW_MESSAGE)
                .when()
                        .post("/")
                .then()
                        .statusCode(200)
                        .contentType("application/xml")
                        .body("SendEmailResponse.SendEmailResult.MessageId", is(not(equalTo(""))));

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(daoMock, times(1)).createMessage(messageCaptor.capture());
        Message savedMessage = messageCaptor.getValue();

        assertThat(savedMessage.getId(), notNullValue());
        assertThat(savedMessage.getDateReceived(), is(not(nullValue())));
        assertThat(savedMessage.getAction(), equalTo(Action.SEND_RAW_EMAIL));
        assertThat(savedMessage.getSubject(), equalTo("Hello email tester!"));
        assertThat(savedMessage.getSource(), equalTo("sender@example.com"));
        assertThat(savedMessage.getDestination(), equalTo(singletonList("example@gmail.com")));
        assertThat(savedMessage.getReplyToAddresses(), equalTo(singletonList("sender@example.com")));
        assertThat(savedMessage.getRawContent(), equalTo(RAW_MESSAGE));
        assertThat(savedMessage.getTextContent(), equalTo("Hello, email tester!\n"));
        assertThat(savedMessage.getHtmlContent(), equalTo("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Hello</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    Hello, email tester!\n" +
                "</body>\n" +
                "</html>\n"));
    }

    @Test
    public void itShouldReturnErrorWhenIncorrectActionGiven() {
        RestAssured
                .given()
                        .param("Action", "DeafBeef")
                        .param("RawMessage.Data", RAW_MESSAGE)
                .when()
                        .post("/")
                .then()
                        .statusCode(400)
                        .contentType("application/xml")
                        .body("Error.Message", equalTo("Invalid action: DeafBeef"));
    }
}
