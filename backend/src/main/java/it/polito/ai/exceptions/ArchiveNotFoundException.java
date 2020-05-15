package it.polito.ai.exceptions;

public class ArchiveNotFoundException extends Exception{
    public ArchiveNotFoundException(String archiveId) {
        super("Archive " + archiveId + " not found.");
    }
}
