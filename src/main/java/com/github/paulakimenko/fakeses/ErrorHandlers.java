package com.github.paulakimenko.fakeses;

import com.github.paulakimenko.fakeses.ex.InvalidActionException;
import com.github.paulakimenko.fakeses.models.Data;
import com.github.paulakimenko.fakeses.models.Error;
import com.github.paulakimenko.fakeses.utils.Json;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

import java.util.Collections;

import static java.lang.String.format;

public final class ErrorHandlers {
    private ErrorHandlers() {}

    public static ExceptionHandler badRequestHandler = (Exception exception, Request request, Response response) -> {
        Error error = new Error()
                .setTitle("Bad Request.")
                .setDetail(exception.getMessage());
        response.type(Type.APPLICATION_JSON);
        response.body(Json.toJson(new Data<>().setErrors(Collections.singletonList(error))));
        response.status(400);
    };

    public static ExceptionHandler notFoundHandler = (Exception exception, Request request, Response response) -> {
        Error error = new Error()
                .setTitle("Not found.")
                .setDetail(exception.getMessage());
        response.type(Type.APPLICATION_JSON);
        response.body(Json.toJson(new Data<>().setErrors(Collections.singletonList(error))));
        response.status(404);
    };

    public static ExceptionHandler invalidActionHandler = (Exception exception, Request request, Response response) -> {
        String action = exception instanceof InvalidActionException
                ? ((InvalidActionException) exception).getActionName()
                : "unknown";
        response.type(Type.APPLICATION_XML);
        response.body(sesError("InvalidAction", "Invalid action: " + action));
        response.status(400);
    };

    private static String sesError(String code, String message) {
        return format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<Error>\n" +
                        "  <Code>%s</Code>\n" +
                        "  <Message>%s</Message>\n" +
                        "</Error>",
                code, message
        );
    }
}
