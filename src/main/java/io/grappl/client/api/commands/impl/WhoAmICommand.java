package io.grappl.client.api.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.commands.Command;

public class WhoAmICommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {

        if(grappl == null) {
            Application.getLog().log("A twinkle in the void tells you this command only works once you've started a Grappl.");
        } else {
            if (grappl.getAuthentication().isLoggedIn()) {
                Application.getLog().log("You are logged in as: " + grappl.getAuthentication().getUsername());
            } else {
                Application.getLog().log("You are not logged in, so you aren't anyone.");
            }
        }
    }
}
