package com.github.paulakimenko.fakeses;

import com.github.paulakimenko.fakeses.models.Action;
import com.github.paulakimenko.fakeses.models.Message;

import java.util.UUID;

import static java.util.Arrays.asList;

public abstract class BaseSESMockTest {
    protected static final String RAW_MESSAGE = "Q29udGVudC1UeXBlOiBtdWx0aXBhcnQvYWx0ZXJuYXRpdmU7DQogYm91bmRhcnk9Ii0tL" +
            "S1zaW5pa2FlbC0/PV8xLTE0ODAzNDk5MDc0NDYwLjE3MjA3NTU4MDMyMzUxNTQ0Ig0KRnJvbTogc2VuZGVyQGV4YW1wbGUuY29tDQpU" +
            "bzogZXhhbXBsZUBnbWFpbC5jb20NClN1YmplY3Q6IEhlbGxvIGVtYWlsIHRlc3RlciENCk1lc3NhZ2UtSUQ6IDxkMzg3MjBjMi1jNTU" +
            "4LWViYjktMzlhOC00MDgzZjdkZTM0NGFAZXhhbXBsZS5jb20+DQpYLU1haWxlcjogbm9kZW1haWxlciAoMi42LjQ7ICtodHRwczovL2" +
            "5vZGVtYWlsZXIuY29tLzsgU0VTLzEuNC4wKQ0KRGF0ZTogTW9uLCAyOCBOb3YgMjAxNiAxNjoxODoyNyArMDAwMA0KTUlNRS1WZXJza" +
            "W9uOiAxLjANCg0KLS0tLS0tc2luaWthZWwtPz1fMS0xNDgwMzQ5OTA3NDQ2MC4xNzIwNzU1ODAzMjM1MTU0NA0KQ29udGVudC1UeXBl" +
            "OiB0ZXh0L3BsYWluDQpDb250ZW50LVRyYW5zZmVyLUVuY29kaW5nOiA3Yml0DQoNCkhlbGxvLCBlbWFpbCB0ZXN0ZXIhCg0KLS0tLS0" +
            "tc2luaWthZWwtPz1fMS0xNDgwMzQ5OTA3NDQ2MC4xNzIwNzU1ODAzMjM1MTU0NA0KQ29udGVudC1UeXBlOiB0ZXh0L2h0bWwNCkNvbn" +
            "RlbnQtVHJhbnNmZXItRW5jb2Rpbmc6IDdiaXQNCg0KPCFET0NUWVBFIGh0bWw+CjxodG1sIGxhbmc9ImVuIj4KPGhlYWQ+CiAgICA8b" +
            "WV0YSBjaGFyc2V0PSJVVEYtOCI+CiAgICA8dGl0bGU+SGVsbG88L3RpdGxlPgo8L2hlYWQ+Cjxib2R5PgogICAgSGVsbG8sIGVtYWls" +
            "IHRlc3RlciEKPC9ib2R5Pgo8L2h0bWw+Cg0KLS0tLS0tc2luaWthZWwtPz1fMS0xNDgwMzQ5OTA3NDQ2MC4xNzIwNzU1ODAzMjM1MTU" +
            "0NC0tDQo=";

    protected static Message generateTestMessage() {
        return new Message()
                .setId(UUID.randomUUID())
                .setAction(Action.SEND_EMAIL)
                .setDateReceived(System.currentTimeMillis())
                .setSubject("Subject " + new Object().hashCode())
                .setSource("source@gmail.com")
                .setDestination(asList("first@gmail.com", "second@gmail.com"))
                .setReplyToAddresses(asList("first@gmail.com", "second@gmail.com"))
                .setTextContent("Hello!")
                .setHtmlContent("<!DOCTYPE html>");
    }
}
