package io.grappl.client.impl.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

public class SetStaticPortCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {
        if (state.getAuthentication().isLoggedIn()) {
            try {
                if (state.getAuthentication().isPremium()) {
                    state.getAuthentication().getAuthDataOutputStream().writeByte(2);
                    state.getAuthentication().getAuthDataOutputStream().writeInt(Integer.parseInt(args[1]));
                    Application.getLog().log("Your port was set to: " + Integer.parseInt(args[1]));
                } else {
                    Application.getLog().log("You are not an alpha tester, so you can't set static ports.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Application.getLog().log("You are not logged in.");
        }
    }

    @Override
    public String getDescription() {
        return "Sets the user's premium static port.";
    }
}
