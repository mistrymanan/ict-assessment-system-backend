package com.cdad.project.userservice.exceptions;

public class NotAuthorized extends Exception {
    public NotAuthorized(String message) {
        super(message);
    }
}
