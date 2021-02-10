package fr.maxime.eventplanner.exceptions;

public class AppUserAlreadyExistException extends Exception {

    public AppUserAlreadyExistException(String message) {
        super(message);
    }
}
