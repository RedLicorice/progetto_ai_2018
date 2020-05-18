package it.polito.ai.exceptions;

public class NotEnoughFundsException extends Exception {
    public NotEnoughFundsException(String username, Double amount){
        super("User " + username + " does not have enough money! (Required: "+amount.toString()+")");
    }
}
