package com.github.paulakimenko.fakeses.controllers;

import com.github.paulakimenko.fakeses.dao.MessagesDAO;
import com.github.paulakimenko.fakeses.models.Action;
import com.github.paulakimenko.fakeses.models.Message;
import com.github.paulakimenko.fakeses.utils.RawMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;

public class SESController {
    private static final Logger log = LoggerFactory.getLogger(SESController.class);

    private final MessagesDAO messagesDAO;

    public SESController(MessagesDAO messagesDAO) {
        this.messagesDAO = messagesDAO;
    }

    public Object postMessage(Request request, Response response) throws Exception {
        log.debug("Received SES message, params '{}', query '{}'", request.params(), request.queryString());

        UUID uuid = UUID.randomUUID();
        Message message = parseMessage(UUID.randomUUID(), request.queryParams(), request.queryMap());
        messagesDAO.createMessage(message);

        response.status(200);

        return sesResponse(uuid);
    }

    private static Message parseMessage(UUID uuid, Set<String> paramsSet, QueryParamsMap queryMap) {
        Action action = Action.fromAWSField(queryMap.get("Action").value());
        long dateReceived = currentTimeMillis() / 1000;

        if (Action.SEND_EMAIL.equals(action)) {
            return new Message()
                    .setId(uuid)
                    .setAction(action)
                    .setDateReceived(dateReceived)
                    .setSubject(queryMap.get("Message.Subject.Data").value())
                    .setSource(queryMap.get("Source").value())
                    .setDestination(parseArrayParam("Destination.ToAddresses.member", paramsSet, queryMap))
                    .setReplyToAddresses(parseArrayParam("ReplyToAddresses.member", paramsSet, queryMap))
                    .setTextContent(queryMap.get("Message.Body.Text.Data").value())
                    .setHtmlContent(queryMap.get("Message.Body.Html.Data").value());
        } else {
            return RawMessageParser.parse(uuid, action, dateReceived, queryMap.get("RawMessage.Data").value());
        }
    }

    private static List<String> parseArrayParam(String nameSegment, Set<String> paramsSet, QueryParamsMap queryMap) {
        return paramsSet
                .stream()
                .filter(param -> param.contains(nameSegment))
                .map(param -> queryMap.get(param).value())
                .collect(toList());
    }

    private static String sesResponse(UUID messageId) {
        return format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<SendEmailResponse xmlns=\"http://ses.amazonaws.com/doc/2010-12-01/\">\n" +
                        "  <SendEmailResult>\n" +
                        "    <MessageId>%s</MessageId>\n" +
                        "  </SendEmailResult>\n" +
                        "</SendEmailResponse>",
                messageId.toString()
        );
    }
}
