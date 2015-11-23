package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

public class WhoAmICommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {

        if(state == null || state.getAuthentication() == null) {
            Application.getLog().log("A twinkle in the void tells you this command only works once you've started a Grappl. (init)");
        } else {
            if (state.getAuthentication().isLoggedIn()) {
                Application.getLog().log("You are logged in as: " + state.getAuthentication().getUsername());
            } else {
                Application.getLog().log("You are not logged in, so you aren't anyone.");
            }
        }
    }
}
