package io.grappl.client.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

public class WhoAmICommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        if(grappl == null) {
            Application.getClientLog().log("A twinkle in the void tells you this command only works once you've started a Grappl.");
        } else {
            if (grappl.getAuthentication().isLoggedIn()) {
                Application.getClientLog().log("You are logged in as: " + grappl.getAuthentication().getUsername());
            } else {
                Application.getClientLog().log("You are not logged in, so you aren't anyone.");
            }
        }
    }
}
