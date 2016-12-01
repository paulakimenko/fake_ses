package com.github.paulakimenko.fakeses.models;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Error {
    @SerializedName("title")
    private String title;
    @SerializedName("detail")
    private String detail;

    public Error() {}

    public String getTitle() {
        return title;
    }

    public Error setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDetail() {
        return detail;
    }

    public Error setDetail(String detail) {
        this.detail = detail;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Error{");
        sb.append("title='").append(title).append('\'');
        sb.append(", detail='").append(detail).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Error error = (Error) o;
        return Objects.equals(title, error.title) &&
                Objects.equals(detail, error.detail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, detail);
    }
}
