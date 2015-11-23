package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.error.AuthenticationFailureException;
import io.grappl.client.impl.stable.Authentication;

public class LoginCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {

        ApplicationState applicationState = Application.getApplicationState();

        final String username = args[1];
        final String password = args[2];

        Authentication authentication = new Authentication();

        try {
            authentication.login(username, Authentication.formatPassword(password));
        } catch (AuthenticationFailureException e) {
            Application.getLog().log(e.getMessage());
        }

        if(authentication.isLoggedIn()) {
            Application.getLog().log("Logged in as " + username);
            applicationState.useAuthentication(authentication);
        } else {
            Application.getLog().log("Use command 'register' to make an account (launches web browser).");
        }
    }
}
