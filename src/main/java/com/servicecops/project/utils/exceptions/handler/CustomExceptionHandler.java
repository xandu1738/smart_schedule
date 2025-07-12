package com.servicecops.project.utils.exceptions.handler;
import com.servicecops.project.utils.OperationReturnObject;
import com.servicecops.project.utils.exceptions.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    @ExceptionHandler(value = IllegalArgumentException.class)
    private OperationReturnObject illegalArgument(IllegalArgumentException e) {
        e.printStackTrace();
        OperationReturnObject oro = new OperationReturnObject();
        oro.setReturnCodeAndReturnMessage(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return oro;
    }

    @ExceptionHandler(value = IllegalStateException.class)
    private OperationReturnObject illegalState(IllegalStateException e) {
        e.printStackTrace();
        OperationReturnObject oro = new OperationReturnObject();
        oro.setReturnCodeAndReturnMessage(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return oro;
    }

    @ExceptionHandler(value = Exception.class)
    private OperationReturnObject exception(Exception e) {
        e.printStackTrace();
        OperationReturnObject oro = new OperationReturnObject();
        oro.setReturnCodeAndReturnMessage(400, e.getMessage());
        return oro;
    }

    @ExceptionHandler(value = RuntimeException.class)
    private OperationReturnObject runtimeException(Exception e) {
        e.printStackTrace();
        OperationReturnObject oro = new OperationReturnObject();
        oro.setReturnCodeAndReturnMessage(400, e.getMessage());
        return oro;
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    private OperationReturnObject expiredOtp(ExpiredJwtException e) {
        e.printStackTrace();
        OperationReturnObject oro = new OperationReturnObject();
        oro.setReturnCodeAndReturnMessage(460, e.getMessage());
        return oro;
    }

    @ExceptionHandler(value = NoSuchElementException.class)
    private OperationReturnObject noSuchElementOtp(NoSuchElementException e) {
        e.printStackTrace();
        OperationReturnObject oro = new OperationReturnObject();
        oro.setReturnCodeAndReturnMessage(404, e.getMessage());
        return oro;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private OperationReturnObject invalidMethodArgException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        List<String> errors = e.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
        OperationReturnObject oro = new OperationReturnObject();
        oro.setReturnCodeAndReturnMessage(406, "Please provide a valid input type.");
        oro.setReturnObject(errors);
        return oro;
    }

    @ExceptionHandler(TypeMismatchException.class)
    public OperationReturnObject handleTypeMismatchException(TypeMismatchException ex) {
        String detail = ex.getMessage();
        OperationReturnObject oro = new OperationReturnObject();
        oro.setReturnCodeAndReturnMessage(400, detail);
        return oro;
    }
}

