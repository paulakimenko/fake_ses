package com.github.paulakimenko.fakeses.utils;

import com.google.gson.Gson;

public final class Json {
    private static final Gson GSON = new Gson();

    public static String toJson(Object src) {
        return GSON.toJson(src);
    }
}
