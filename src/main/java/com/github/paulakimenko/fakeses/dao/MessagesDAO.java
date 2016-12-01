package com.github.paulakimenko.fakeses.dao;

import com.github.paulakimenko.fakeses.models.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public interface MessagesDAO {
    void createMessage(Message message);
    Optional<Message> getMessageById(UUID id);
    List<Message> getMessages(Predicate<Message> searchCriteria);
    void deleteAllMessages();
}
