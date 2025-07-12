package com.servicecops.project.utils.exceptions;

public class ExpiredOtpException extends Exception{
    public ExpiredOtpException(String message) {
        super(message);
    }
}
