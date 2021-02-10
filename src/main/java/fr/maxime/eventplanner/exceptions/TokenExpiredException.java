package fr.maxime.eventplanner.exceptions;

public class TokenExpiredException extends Exception {

    public TokenExpiredException(String message) {
        super(message);
    }
}
