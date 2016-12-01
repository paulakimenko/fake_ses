package com.github.paulakimenko.fakeses;

import com.github.paulakimenko.fakeses.controllers.MessagesController;
import com.github.paulakimenko.fakeses.controllers.SESController;
import com.github.paulakimenko.fakeses.dao.MessagesDAO;
import com.github.paulakimenko.fakeses.dao.MessagesFileDAO;
import com.github.paulakimenko.fakeses.utils.Arguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static spark.Spark.port;
import static spark.Spark.threadPool;

public class FakeSES {
    private static final Logger log = LoggerFactory.getLogger(FakeSES.class);

    public static void main(String[] args) {
        runApp(getArguments());
    }

    public static void runApp(Arguments arguments) {
        log.info("Init Application with given params '{}'", arguments);

        MessagesDAO messagesDAO = initMessagesDAO(arguments.getWorkDir());

        port(arguments.getPort());
        threadPool(arguments.getThreadCount());

        SESController sesController = new SESController(messagesDAO);
        MessagesController messagesController = new MessagesController(messagesDAO);

        Router router = new Router(sesController, messagesController);
        router.setup();
    }

    private static Arguments getArguments() {
        try {
            return Arguments.getFromEnviroment();
        } catch (RuntimeException e) {
            exitWithError(1, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static MessagesDAO initMessagesDAO(String workDir) {
        try {
            return MessagesFileDAO.prepareFor(workDir);
        } catch (IOException e) {
            exitWithError(2, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void exitWithError(int code, String error) {
        log.error("Error has been occurred '{}'! Exit {}.", error, code);
        System.exit(code);
    }
}
