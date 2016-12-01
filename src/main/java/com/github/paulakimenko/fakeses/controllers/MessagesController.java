package com.github.paulakimenko.fakeses.controllers;

import com.github.paulakimenko.fakeses.utils.Json;
import com.github.paulakimenko.fakeses.utils.MessageFilter;
import com.github.paulakimenko.fakeses.ex.NotFoundException;
import com.github.paulakimenko.fakeses.dao.MessagesDAO;
import com.github.paulakimenko.fakeses.models.Data;
import com.github.paulakimenko.fakeses.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

public class MessagesController {
    private static final Logger log = LoggerFactory.getLogger(MessagesController.class);

    private final MessagesDAO messagesDAO;

    public MessagesController(MessagesDAO messagesDAO) {
        this.messagesDAO = messagesDAO;
    }

    public Object getMessageById(Request request, Response response) throws Exception {
        log.debug("Get message by id, params '{}', query '{}'", request.params(), request.queryString());

        String strId = Optional.of(request.params(":id"))
                .orElseThrow(() -> new IllegalArgumentException("ID has not been chosen!"));

        UUID id = UUID.fromString(strId);

        Message message = messagesDAO
                .getMessageById(id)
                .orElseThrow(() -> new NotFoundException("Message", id));

        response.status(200);

        return Json.toJson(new Data<>().setData(message));
    }

    public Object getMessages(Request request, Response response) throws Exception {
        log.debug("Get messages, params '{}', query '{}'", request.params(), request.queryString());

        Map<String, String> argsMap = argsToMap(request.queryParams("filter"));

        List<Message> messages = messagesDAO.getMessages(MessageFilter.fromArgsMap(argsMap));

        response.status(200);

        return Json.toJson(new Data<>().setData(messages));
    }

    public Object deleteAllMessages(Request request, Response response) throws Exception {
        log.debug("Delete all messages, params '{}', query '{}'", request.params(), request.queryString());

        messagesDAO.deleteAllMessages();

        response.status(204);

        return "";
    }

    private static Map<String, String> argsToMap(String args) {
        if (args == null || args.length() == 0) {
            return new HashMap<>();
        }

        Function<String, Map.Entry<String, String>> stringToEntries = keyValuePair -> {
            String[] keyValue = keyValuePair.split(":");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException(format("Incorrect pair %s", keyValuePair));
            }
            return new AbstractMap.SimpleEntry<>(keyValue[0], keyValue[1]);
        };

        return Stream.of(args.split(";"))
                .map(stringToEntries)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
