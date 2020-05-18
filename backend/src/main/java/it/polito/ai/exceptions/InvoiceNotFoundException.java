package it.polito.ai.exceptions;

public class InvoiceNotFoundException extends Exception {
    public InvoiceNotFoundException(String invoiceId, String username){
        super("Invoice " + invoiceId + " not found for user " + username);
    }
}
