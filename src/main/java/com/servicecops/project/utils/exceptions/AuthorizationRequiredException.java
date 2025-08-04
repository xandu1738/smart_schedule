package com.servicecops.project.utils.exceptions;

public class AuthorizationRequiredException extends Exception {
    public AuthorizationRequiredException(String message) {
        super(message);
    }
}
