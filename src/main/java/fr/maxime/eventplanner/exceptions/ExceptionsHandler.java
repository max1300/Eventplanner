package fr.maxime.eventplanner.exceptions;

import fr.maxime.eventplanner.dtos.CustomHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDate;

@RestControllerAdvice
public class ExceptionsHandler {
    public static final Logger LOG = LoggerFactory.getLogger(ExceptionsHandler.class);


    @ExceptionHandler(AppUserNotFoundException.class)
    public ResponseEntity<CustomHttpResponse> userNotFound(AppUserNotFoundException exception) {
        LOG.debug(exception.getMessage());
        return getHttpResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public ResponseEntity<CustomHttpResponse> usernameExistException(UsernameAlreadyExistException exception) {
        LOG.debug(exception.getMessage());
        return getHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<CustomHttpResponse> emailExistException(EmailAlreadyExistException exception) {
        LOG.debug(exception.getMessage());
        return getHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmailNotValidException.class)
    public ResponseEntity<CustomHttpResponse> emailValidException(EmailNotValidException exception) {
        LOG.debug(exception.getMessage());
        return getHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(TokenAlreadyConfirmedException.class)
    public ResponseEntity<CustomHttpResponse> tokenConfirmedException(TokenAlreadyConfirmedException exception) {
        LOG.debug(exception.getMessage());
        return getHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<CustomHttpResponse> tokenExpiredException(TokenExpiredException exception) {
        LOG.debug(exception.getMessage());
        return getHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private ResponseEntity<CustomHttpResponse> getHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new CustomHttpResponse(LocalDate.now(), httpStatus.value(), httpStatus,
                        httpStatus.getReasonPhrase().toUpperCase(),
                        message.toUpperCase()), httpStatus
        );

    }
}
