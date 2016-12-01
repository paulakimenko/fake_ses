package com.github.paulakimenko.fakeses.ex;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    private final String objectName;
    private final UUID id;

    public NotFoundException(String objectName, UUID id) {
        super(objectName + " with id " + id.toString() + " not found!");
        this.objectName = objectName;
        this.id = id;
    }

    public String getObjectName() {
        return objectName;
    }

    public UUID getId() {
        return id;
    }
}
