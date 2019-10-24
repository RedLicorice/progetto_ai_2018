package it.polito.ai.models;

public class RestGenericResponse {

    String message;

    public RestGenericResponse(String msg) {
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
