package fr.maxime.eventplanner.exceptions;

import fr.maxime.eventplanner.dtos.CustomHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.time.LocalDate;


@RestControllerAdvice
public class ExceptionsHandler {
    public static final Logger LOG = LoggerFactory.getLogger(ExceptionsHandler.class);



    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<CustomHttpResponse> itemNotFound(NoResultException exception) {
        LOG.debug(exception.getMessage());
        return getHttpResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CustomHttpResponse> stateException(NoResultException exception) {
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
