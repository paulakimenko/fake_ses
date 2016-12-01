package com.github.paulakimenko.fakeses.utils;

import com.github.paulakimenko.fakeses.Type;
import com.github.paulakimenko.fakeses.ex.RawMessageParserException;
import com.github.paulakimenko.fakeses.models.Action;
import com.github.paulakimenko.fakeses.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

import static com.github.paulakimenko.fakeses.Type.MULTIPART_ALL;
import static java.util.stream.Collectors.toList;

public final class RawMessageParser {
    private static final Logger log = LoggerFactory.getLogger(RawMessageParser.class);

    private RawMessageParser() {}

    public static Message parse(UUID id, Action action, long dateReceived, String rawBody) {
        MimeMessage mimeMessage = toMimeMessage(rawBody);
        try {
            ParsedBody parsedBody = getBody(mimeMessage.getContent());
            return new Message()
                    .setId(id)
                    .setAction(action)
                    .setDateReceived(dateReceived)
                    .setSubject(mimeMessage.getSubject())
                    .setSource(getFirstAddress(mimeMessage.getFrom()))
                    .setDestination(toAddressList(mimeMessage.getAllRecipients()))
                    .setReplyToAddresses(toAddressList(mimeMessage.getReplyTo()))
                    .setRawContent(rawBody)
                    .setTextContent(parsedBody.getText().orElse(null))
                    .setHtmlContent(parsedBody.getHtml().orElse(null));
        } catch (MessagingException | IOException e) {
            log.error("Error on get MimeMessage properties", e);
            throw new RawMessageParserException(e);
        }
    }

    private static MimeMessage toMimeMessage(String rawBody) {
        byte[] bytes = Base64.getDecoder().decode(rawBody);
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            return new MimeMessage(null, inputStream);
        } catch (IOException | MessagingException e) {
            log.error("Error on decoding MimeMessage", e);
            throw new RawMessageParserException(e);
        }
    }

    private static String getFirstAddress(Address[] addresses) {
        return Stream.of(Optional.ofNullable(addresses).orElse(new Address[0]))
                .map(Address::toString)
                .findFirst()
                .orElse("");
    }

    private static List<String> toAddressList(Address[] addresses) {
        return Stream.of(Optional.ofNullable(addresses).orElse(new Address[0]))
                .map(Address::toString)
                .collect(toList());
    }

    private static ParsedBody getBody(Object content) {
        return !(content instanceof Multipart)
                ? new ParsedBody((String) content, null)
                : streamOfMultipart((Multipart) content)
                        .map(RawMessageParser::getPartBody)
                        .reduce((first, second) -> new ParsedBody(
                                first.getText().orElse("") + second.getText().orElse(""),
                                first.getHtml().orElse("") + second.getHtml().orElse("")
                        ))
                        .orElse(new ParsedBody(null, null));
    }

    private static Stream<Part> streamOfMultipart(Multipart multipart) {
        Stream.Builder<Part> streamBuilder = Stream.builder();
        try {
            for (int i = 0; i < multipart.getCount(); i++) {
                Part part = multipart.getBodyPart(i);
                if (part.isMimeType(Type.TEXT_ALL)) {
                    streamBuilder.add(part);
                } else if (part.isMimeType(MULTIPART_ALL)) {
                    streamOfMultipart((Multipart) part.getContent()).forEach(streamBuilder.add(part));
                }
            }
        } catch (MessagingException | IOException e) {
            log.error("Error on parsing multipart to parts", e);
            throw new RawMessageParserException(e);
        }
        return streamBuilder.build();
    }

    private static ParsedBody getPartBody(Part part) {
        try {
            String content = (String) part.getContent();
            return part.isMimeType(Type.TEXT_HTML)
                    ? new ParsedBody(null, content)
                    : new ParsedBody(content, null);
        } catch (MessagingException | IOException e) {
            log.error("Error on get part content", e);
            throw new RawMessageParserException(e);
        }
    }

    private static class ParsedBody {
        String text;
        String html;

        ParsedBody(String text, String html) {
            this.text = text;
            this.html = html;
        }

        Optional<String> getText() {
            return Optional.ofNullable(text);
        }

        Optional<String> getHtml() {
            return Optional.ofNullable(html);
        }
    }
}
