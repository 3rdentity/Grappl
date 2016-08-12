package io.grappl.client.impl.authentication;

import io.grappl.client.api.event.Listener;

public class AuthenticationEvent implements Listener {

    private Authentication authentication;

    public AuthenticationEvent(Authentication authentication) {
        this.authentication = authentication;
    }

    public Authentication getAuthentication() {
        return authentication;
    }
}
