package io.grappl.client.impl.authentication;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to create Authentication instances.
 */
public final class Authenticator {

    /* The location of the core server */
    protected static final String CORE_DOMAIN = "grappl.io";

    /* The port of the auth server within the core server */
    protected static final int AUTHENTICATION_PORT = 25571;

    private static final List<AuthenticationListener> authenticationListeners = new ArrayList<>();

    /**
     * Takes a users's credentials and creates a logged in Authentication state with the core server.
     * @param username the user's Grappl username
     * @param password the user's Grappl password
     * @return a (hopefully logged in) authentication state
     * @throws AuthenticationException an exception if it fails to log the user in
     */
    public static Authentication login(String username, char[] password) throws AuthenticationException {
        Authentication authentication = new Authentication();

        authentication.createSession(username, password);

        for(AuthenticationListener authenticationListener : authenticationListeners) {
            authenticationListener.authenticate(new AuthenticationEvent(authentication));
        }

        return authentication;
    }

    public static char[] formatPassword(String password) {
        return (password.hashCode() + "").toCharArray();
    }

    public static void addAuthenticationListener(AuthenticationListener authenticationListener) {
        authenticationListeners.add(authenticationListener);
    }

    public static void removeAuthenticationListener(AuthenticationListener authenticationListener) {
        authenticationListeners.remove(authenticationListener);
    }
}
