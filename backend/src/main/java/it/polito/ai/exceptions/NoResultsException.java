package it.polito.ai.exceptions;

public class NoResultsException extends Exception {
    public NoResultsException(){
        super("No results found.");
    }
}
