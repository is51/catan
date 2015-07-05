package catan.controllers;

import catan.domain.transfer.output.ErrorDetails;
import catan.exception.UserException;
import catan.services.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@ControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(UserException.class)
    @RequestMapping(value = "error",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ErrorDetails> handleUserExceptions(UserException e) {
        //TODO: need to improve error handling, doesn't work in manual mode
        HttpStatus status;
        ErrorDetails details;

        if (UserServiceImpl.ERROR_CODE_TOKEN_INVALID.equals(e.getErrorCode())) {
            status = HttpStatus.FORBIDDEN;
            details = null;
        } else {
            status = HttpStatus.BAD_REQUEST;
            details = new ErrorDetails(e.getErrorCode());
        }

        return new ResponseEntity<ErrorDetails>(details, status);
    }
}
