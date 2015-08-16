package catan.controllers;

import catan.domain.transfer.output.common.ErrorDetails;
import catan.domain.exception.AuthenticationException;
import catan.domain.exception.GameException;
import catan.domain.exception.UserException;
import catan.domain.exception.WrongPathException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(WrongPathException.class)
    public ResponseEntity handleWrongPathExceptions(WrongPathException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        return new ResponseEntity(status);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity handleAuthenticationExceptions(AuthenticationException e) {
        HttpStatus status = HttpStatus.FORBIDDEN;

        return new ResponseEntity(status);
    }

    @ExceptionHandler(UserException.class)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ErrorDetails> handleUserExceptions(UserException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorDetails details = new ErrorDetails(e.getErrorCode());


        return new ResponseEntity<ErrorDetails>(details, status);
    }

    @ExceptionHandler(GameException.class)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ErrorDetails> handleGameExceptions(GameException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorDetails details = new ErrorDetails(e.getErrorCode());

        return new ResponseEntity<ErrorDetails>(details, status);
    }
}
