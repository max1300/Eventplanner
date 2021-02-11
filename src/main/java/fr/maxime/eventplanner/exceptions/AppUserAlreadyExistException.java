package fr.maxime.eventplanner.exceptions;

public class AppUserAlreadyExistException extends RuntimeException {

    public AppUserAlreadyExistException(String message) {
        super(message);
    }
}
