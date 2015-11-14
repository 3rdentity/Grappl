package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.stable.ApplicationState;
import io.grappl.client.impl.stable.Authentication;

public class LoginCommand implements Command{

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        ApplicationState applicationState = Application.getApplicationState();

        final String username = args[1];
        final String password = args[2];

        Authentication authentication = new Authentication();
        authentication.login(username, (password.hashCode() + "").toCharArray());
        applicationState.useAuthentication(authentication);

        Application.getLog().log("Logged in as " + username);
    }
}
