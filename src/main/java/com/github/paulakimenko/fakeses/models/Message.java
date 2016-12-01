package com.github.paulakimenko.fakeses.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Message {
    @Expose
    @SerializedName("id")
    private UUID id;
    @Expose
    @SerializedName("action")
    private Action action;
    @Expose
    @SerializedName("subject")
    private String subject;
    @Expose
    @SerializedName("source")
    private String source;
    @Expose
    @SerializedName("destination")
    private List<String> destination;
    @Expose
    @SerializedName("reply_to_addresses")
    private List<String> replyToAddresses;
    @Expose
    @SerializedName("date_received")
    private long dateReceived;
    @SerializedName("html_content")
    private String htmlContent;
    @SerializedName("text_content")
    private String textContent;
    @SerializedName("raw_content")
    private String rawContent;

    public UUID getId() {
        return id;
    }

    public Message setId(UUID id) {
        this.id = id;
        return this;
    }

    public Action getAction() {
        return action;
    }

    public Message setAction(Action action) {
        this.action = action;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public Message setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getSource() {
        return source;
    }

    public Message setSource(String source) {
        this.source = source;
        return this;
    }

    public List<String> getDestination() {
        return destination;
    }

    public Message setDestination(List<String> destination) {
        this.destination = destination;
        return this;
    }

    public List<String> getReplyToAddresses() {
        return replyToAddresses;
    }

    public Message setReplyToAddresses(List<String> replyToAddresses) {
        this.replyToAddresses = replyToAddresses;
        return this;
    }

    public long getDateReceived() {
        return dateReceived;
    }

    public Message setDateReceived(long dateReceived) {
        this.dateReceived = dateReceived;
        return this;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public Message setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
        return this;
    }

    public String getTextContent() {
        return textContent;
    }

    public Message setTextContent(String textContent) {
        this.textContent = textContent;
        return this;
    }

    public String getRawContent() {
        return rawContent;
    }

    public Message setRawContent(String rawContent) {
        this.rawContent = rawContent;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("id=").append(id);
        sb.append(", action=").append(action);
        sb.append(", subject='").append(subject).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", destination=").append(destination);
        sb.append(", replyToAddresses=").append(replyToAddresses);
        sb.append(", dateReceived=").append(dateReceived);
        sb.append(", htmlContent='").append(htmlContent).append('\'');
        sb.append(", textContent='").append(textContent).append('\'');
        sb.append(", rawContent='").append(rawContent).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message that = (Message) o;
        return dateReceived == that.dateReceived &&
                Objects.equals(id, that.id) &&
                action == that.action &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(source, that.source) &&
                Objects.equals(destination, that.destination) &&
                Objects.equals(replyToAddresses, that.replyToAddresses) &&
                Objects.equals(htmlContent, that.htmlContent) &&
                Objects.equals(textContent, that.textContent) &&
                Objects.equals(rawContent, that.rawContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, action, subject, source, destination, replyToAddresses, dateReceived, htmlContent,
                textContent, rawContent);
    }
}
