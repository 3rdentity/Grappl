package io.grappl.client.impl.event;

import io.grappl.client.impl.authentication.Authentication;

public class AuthenticationEvent {

    private Authentication authentication;
    private boolean success;

    public AuthenticationEvent(Authentication authentication, boolean success) {
        this.authentication = authentication;
        this.success = success;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public boolean isSuccess() {
        return success;
    }
}
