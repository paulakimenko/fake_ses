package com.github.paulakimenko.fakeses.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class Data<T> {
    @SerializedName("data")
    private T data;
    @SerializedName("errors")
    private List<Error> errors;

    public Data() {}

    public T getData() {
        return data;
    }

    public Data setData(T data) {
        this.data = data;
        return this;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public Data setErrors(List<Error> errors) {
        this.errors = errors;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Data{");
        sb.append("data=").append(data);
        sb.append(", errors=").append(errors);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data<?> data1 = (Data<?>) o;
        return Objects.equals(data, data1.data) &&
                Objects.equals(errors, data1.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, errors);
    }
}
