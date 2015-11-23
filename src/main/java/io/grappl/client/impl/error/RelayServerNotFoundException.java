package io.grappl.client.impl.error;

public class RelayServerNotFoundException extends Exception {

    public RelayServerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RelayServerNotFoundException(String message) {
        super(message);
    }
}
