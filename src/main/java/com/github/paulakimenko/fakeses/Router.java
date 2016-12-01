package com.github.paulakimenko.fakeses;

import com.github.paulakimenko.fakeses.controllers.MessagesController;
import com.github.paulakimenko.fakeses.controllers.SESController;
import com.github.paulakimenko.fakeses.ex.InvalidActionException;
import com.github.paulakimenko.fakeses.ex.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class Router {
    private static final String API_PATH = "/api";

    private static final Logger log = LoggerFactory.getLogger(MessagesController.class);

    private final SESController sesController;
    private final MessagesController messagesController;

    public Router(SESController sesController, MessagesController messagesController) {
        this.sesController = sesController;
        this.messagesController = messagesController;
    }

    public void setup() {
        log.debug("Setup routes");

        exception(InvalidActionException.class, ErrorHandlers.invalidActionHandler);
        exception(NotFoundException.class, ErrorHandlers.notFoundHandler);
        exception(IllegalArgumentException.class, ErrorHandlers.badRequestHandler);
        exception(IllegalStateException.class, ErrorHandlers.badRequestHandler);

        staticFiles.externalLocation("./public");

        post("/", sesController::postMessage);

        get(API_PATH + "/messages", messagesController::getMessages);
        get(API_PATH + "/messages/", messagesController::getMessages);

        get(API_PATH + "/messages/:id", messagesController::getMessageById);

        delete(API_PATH + "/messages", messagesController::deleteAllMessages);
        delete(API_PATH + "/messages/", messagesController::deleteAllMessages);

        after(((request, response) -> {
            if (request.url().contains(API_PATH)) {
                response.type(Type.APPLICATION_JSON);
            } else {
                response.type(Type.APPLICATION_XML);
            }
        }));
    }
}
