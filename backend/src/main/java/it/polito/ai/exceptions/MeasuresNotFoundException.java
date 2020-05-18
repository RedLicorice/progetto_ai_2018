package it.polito.ai.exceptions;

public class MeasuresNotFoundException extends Exception{
    public MeasuresNotFoundException(String archiveId) {
        super("No measures found for archive " + archiveId + " .");
    }
}
