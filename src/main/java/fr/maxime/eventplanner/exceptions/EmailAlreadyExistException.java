package fr.maxime.eventplanner.exceptions;

public class EmailAlreadyExistException extends Exception {

    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
