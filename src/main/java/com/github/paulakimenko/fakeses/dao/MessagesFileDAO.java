package com.github.paulakimenko.fakeses.dao;

import com.github.paulakimenko.fakeses.models.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class MessagesFileDAO implements MessagesDAO {
    private static final Logger log = LoggerFactory.getLogger(MessagesFileDAO.class);
    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    private final Path baseDirPath;

    public static MessagesFileDAO prepareFor(String baseDir) throws IOException {
        log.debug("Init FileDAO in '{}'", baseDir);

        Path baseDirPath = Paths.get(baseDir);
        if (!Files.exists(baseDirPath)) {
            Files.createDirectories(baseDirPath);
        }

        return new MessagesFileDAO(baseDirPath);
    }

    private MessagesFileDAO(Path baseDirPath) {
        this.baseDirPath = baseDirPath;
    }

    @Override
    public void createMessage(Message message) {
        log.debug("Save message {}", message);

        try {
            Objects.requireNonNull(message, "Message is null!");
            UUID messageId = message.getId();
            Objects.requireNonNull(messageId, "Message id is null!");

            Path messageDirPath = baseDirPath.resolve(messageId.toString());
            if (!Files.exists(messageDirPath)) {
                Files.createDirectories(messageDirPath);
            }

            writeFile(messageJsonPath(messageDirPath), GSON.toJson(message));

            if (message.getTextContent() != null) {
                writeFile(textContentPath(messageDirPath), message.getTextContent());
            }

            if (message.getHtmlContent() != null) {
                writeFile(htmlContentPath(messageDirPath), message.getHtmlContent());
            }

            if (message.getRawContent() != null) {
                writeFile(rawContentPath(messageDirPath), message.getRawContent());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> getMessageById(UUID id) {
        log.debug("Get message by id '{}'", id);

        return readMessageFromPath(baseDirPath.resolve(id.toString()));
    }

    @Override
    public List<Message> getMessages(Predicate<Message> searchCriteria) {
        log.debug("Get messages with search criteria '{}'", searchCriteria);

        return listFilesInBaseDir()
                .filter(entry -> Files.isDirectory(entry))
                .map(this::readMessageFromPath)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(searchCriteria)
                .sorted((first, second) ->
                        first.getDateReceived() > second.getDateReceived()
                                ? 1
                                : first.getDateReceived() < second.getDateReceived()
                                        ? -1
                                        : 0)
                .collect(toList());
    }

    @Override
    public void deleteAllMessages() {
        log.debug("Delete all messages");

        listFilesInBaseDir().forEach(this::deleteFile);
    }

    private Optional<Message> readMessageFromPath(Path messageDirPath) {
        Path messageInfoPath = messageDirPath.resolve("messageinfo.json");

        if (!Files.exists(messageInfoPath)) {
            return Optional.empty();
        }

        Message message = GSON.fromJson(readFile(messageInfoPath), Message.class);

        Path textContentPath = textContentPath(messageDirPath);
        if (Files.exists(textContentPath)) {
            message.setTextContent(readFile(textContentPath));
        }

        Path htmlContentPath = htmlContentPath(messageDirPath);
        if (Files.exists(htmlContentPath)) {
            message.setHtmlContent(readFile(htmlContentPath));
        }

        Path rawContentPath = rawContentPath(messageDirPath);
        if (Files.exists(rawContentPath)) {
            message.setRawContent(readFile(rawContentPath));
        }

        return Optional.of(message);
    }

    private void writeFile(Path filePath, String content) throws IOException {
        Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
    }

    private String readFile(Path filePath) {
        try {
            return new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteFile(Path filePath) {
        try {
            Files.walkFileTree(filePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<Path> listFilesInBaseDir() {
        try {
            return Files.list(baseDirPath);
        } catch (IOException e) {
            log.error("Error '{}' on list file in base dir '{}'", e, baseDirPath);
            return Stream.empty();
        }
    }

    private static Path messageJsonPath(Path messageDirPath) {
        return messageDirPath.resolve("messageinfo.json");
    }

    private static Path textContentPath(Path messageDirPath) {
        return messageDirPath.resolve("content.txt");
    }

    private static Path htmlContentPath(Path messageDirPath) {
        return messageDirPath.resolve("content.html");
    }

    private static Path rawContentPath(Path messageDirPath) {
        return messageDirPath.resolve("raw.txt");
    }
}
