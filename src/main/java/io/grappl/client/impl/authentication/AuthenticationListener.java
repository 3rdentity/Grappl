package io.grappl.client.impl.authentication;

public interface AuthenticationListener {

    public void authenticate(AuthenticationEvent authenticationEvent);
}
