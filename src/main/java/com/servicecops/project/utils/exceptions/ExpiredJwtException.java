package com.servicecops.project.utils.exceptions;

public class ExpiredJwtException extends Exception{
    public ExpiredJwtException(String message) {
        super(message);
    }
}
