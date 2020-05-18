package it.polito.ai.exceptions;

public class UserHasNoArchivesException extends Exception {
    public UserHasNoArchivesException(String username) {
        super("User " + username + " has no archive.");
    }
}
