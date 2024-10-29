package com.example.scanapp4.Model;

public class StatusWithMessage {
    private Status status;
    private String message;

    public StatusWithMessage(Status status) {
        this.status = status;
    }

    public StatusWithMessage(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
