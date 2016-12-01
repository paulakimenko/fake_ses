package com.github.paulakimenko.fakeses.controllers;

import com.github.paulakimenko.fakeses.models.Message;
import com.jayway.restassured.RestAssured;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;

public class MessagesControllerTest extends BaseControllerTest {
    @Test
    public void itShouldGetMessageById() {
        Message testMessage = generateTestMessage();

        Mockito
                .when(daoMock.getMessageById(testMessage.getId()))
                .thenReturn(Optional.of(testMessage));

        RestAssured
                .when()
                        .get("/api/messages/" + testMessage.getId().toString())
                .then()
                        .statusCode(200)
                        .contentType("application/json")
                        .body(matchesJsonSchemaInClasspath("schemas/message-schema.json"))
                        .body("data.id", is(testMessage.getId().toString()));

        Mockito
                .verify(daoMock, times(1)).getMessageById(testMessage.getId());
    }

    @Test
    public void itShouldReturnErrorWhenGivenNonExitingId() {
        UUID id = UUID.randomUUID();

        Mockito
                .when(daoMock.getMessageById(id))
                .thenReturn(Optional.empty());

        RestAssured
                .when()
                        .get("/api/messages/" + id.toString())
                .then()
                        .statusCode(404)
                        .contentType("application/json")
                        .body(matchesJsonSchemaInClasspath("schemas/api-error-schema.json"))
                        .body("errors[0].title", is("Not found."))
                        .body("errors[0].detail", is(format("Message with id %s not found!", id)));

        Mockito
                .verify(daoMock, times(1)).getMessageById(id);
    }

    @Test
    public void itShouldGetAllMessages() {
        List<Message> testMessages = asList(generateTestMessage(), generateTestMessage());

        Mockito
                .when(daoMock.getMessages(any()))
                .thenReturn(testMessages);

        RestAssured
                .when()
                        .get("/api/messages/")
                .then()
                        .statusCode(200)
                        .contentType("application/json")
                        .body(matchesJsonSchemaInClasspath("schemas/messages-schema.json"))
                        .body("data", hasSize(testMessages.size()))
                        .body("data[0].id", is(testMessages.get(0).getId().toString()));

        Mockito
                .verify(daoMock, times(1)).getMessages(any());
    }

    @Test
    public void itShouldGetMessagesByGivenQuery() {
        List<Message> testMessages = asList(generateTestMessage(), generateTestMessage());

        Mockito
                .when(daoMock.getMessages(any()))
                .thenReturn(testMessages);

        String filter = String.format(
                "subject:%s;destination:%s",
                testMessages.get(0).getSubject(),
                testMessages.get(0).getDestination().get(0)
        );

        RestAssured
                .given()
                        .queryParam("filter", filter)
                .when()
                        .get("/api/messages")
                .then()
                        .statusCode(200)
                        .contentType("application/json")
                        .body(matchesJsonSchemaInClasspath("schemas/messages-schema.json"))
                        .body("data", hasSize(testMessages.size()))
                        .body("data[0].id", is(testMessages.get(0).getId().toString()));

        Mockito
                .verify(daoMock, times(1)).getMessages(any());
    }

    @Test
    public void itShouldReturnErrorWhenIncorrectQueryGiven() {
        RestAssured
                .given()
                        .queryParam("filter", "DeadBeef")
                .when()
                        .get("/api/messages")
                .then()
                        .statusCode(400)
                        .contentType("application/json")
                        .body(matchesJsonSchemaInClasspath("schemas/api-error-schema.json"))
                        .body("errors[0].title", is("Bad Request."))
                        .body("errors[0].detail", is("Incorrect pair DeadBeef"));

        Mockito
                .verify(daoMock, times(0)).getMessages(any());
    }

    @Test
    public void itShouldDeleteAllMessages() {
        RestAssured
                .when()
                        .delete("/api/messages")
                .then()
                        .statusCode(204)
                        .contentType("application/json");

        Mockito
                .verify(daoMock, times(1)).deleteAllMessages();
    }
}
