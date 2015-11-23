package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

public class LogoutCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {

        if(state.getAuthentication() == null) {
            Application.getLog().log(Application.NOT_LOGGED_IN_MESSAGE);
        } else {
            state.getAuthentication().logout();
            Application.getLog().log("Logged out. Bye, " + state.getAuthentication().getUsername() + "!");
        }
    }

    @Override
    public String getDescription() {
        return "Logs a user out of their account";
    }
}
