package it.polito.ai.exceptions;

public class InvoiceNotFoundException extends Exception {
    public InvoiceNotFoundException(String message){
        super(message);
    }
}
