package it.polito.ai.models;

public class RestErrorResponse {

    private String message;

    public RestErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
