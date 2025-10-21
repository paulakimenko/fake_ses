package com.github.paulakimenko.fakeses.controllers;

import com.github.paulakimenko.fakeses.BaseSESMockTest;
import com.github.paulakimenko.fakeses.Router;
import com.github.paulakimenko.fakeses.dao.MessagesDAO;
import com.jayway.restassured.RestAssured;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static spark.Spark.port;
import static spark.Spark.stop;
import static spark.Spark.awaitStop;

public abstract class BaseControllerTest extends BaseSESMockTest {
    private static final int PORT = 8111;

    static MessagesDAO daoMock;

    static {
        RestAssured.baseURI = "http://127.0.0.1";
        RestAssured.port = PORT;
    }

    @BeforeClass
    public static void setUpClass() {
        daoMock = mock(MessagesDAO.class);
        // Ensure Spark is fully stopped before starting to avoid port/route state issues
        stop();
        awaitStop();
        startApp();
    }

    @AfterClass
    public static void tearDownClass() {
        stop();
        awaitStop();
    }

    @After
    public void tearDown() {
        reset(daoMock);
    }

    private static void startApp() {
        port(PORT);

        SESController sesController = new SESController(daoMock);
        MessagesController messagesController = new MessagesController(daoMock);

        Router router = new Router(sesController, messagesController);
        router.setup();
    }
}
