package com.github.paulakimenko.fakeses.models;

import com.github.paulakimenko.fakeses.ex.InvalidActionException;
import com.google.gson.annotations.SerializedName;

import java.util.stream.Stream;

public enum Action {
    @SerializedName("SendEmail")
    SEND_EMAIL("SendEmail"),

    @SerializedName("SendRawEmail")
    SEND_RAW_EMAIL("SendRawEmail");

    private final String awsField;

    public static Action fromAWSField(String awsField) {
        return Stream.of(values())
                .filter(v -> v.awsField.equals(awsField))
                .findFirst()
                .orElseThrow(() -> new InvalidActionException(awsField));
    }

    Action(String awsField) {
        this.awsField = awsField;
    }

    public String getAwsField() {
        return awsField;
    }
}
