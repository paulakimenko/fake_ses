package com.github.paulakimenko.fakeses.ex;

import static java.lang.String.format;

public class InvalidActionException extends RuntimeException {
    private final String actionName;

    public InvalidActionException(String actionName) {
        super(format("Unknown action : %s!", actionName));
        this.actionName = actionName;
    }

    public String getActionName() {
        return actionName;
    }
}
