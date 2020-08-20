package com.postitapplications.user.exception;

import com.postitapplications.exception.ExceptionResponseBody;
import com.postitapplications.exception.exceptions.NullOrEmptyException;
import com.postitapplications.exception.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class UserExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {NullOrEmptyException.class, NullPointerException.class})
    public ResponseEntity<Object> handleBadRequestException(Exception exception) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ExceptionResponseBody exceptionResponseBody = new ExceptionResponseBody(badRequest,
            exception.getMessage());

        return new ResponseEntity<>(exceptionResponseBody, badRequest);
    }

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        ExceptionResponseBody exceptionResponseBody = new ExceptionResponseBody(notFound,
            exception.getMessage());

        return new ResponseEntity<>(exceptionResponseBody, notFound);
    }
}
