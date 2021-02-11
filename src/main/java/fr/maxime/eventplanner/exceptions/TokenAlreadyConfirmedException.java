package fr.maxime.eventplanner.exceptions;

public class TokenAlreadyConfirmedException extends RuntimeException {

    public TokenAlreadyConfirmedException(String message) {
        super(message);
    }
}
