package io.grappl.client.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

public class WhoAmICommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        if(grappl == null) {
            Application.getClientLog().log("Grappl has not been initialized");
        } else {
            if (grappl.getAuthentication().isLoggedIn()) {
                Application.getClientLog().log("You are logged in as: " + grappl.getAuthentication().getUsername());
            } else {
                Application.getClientLog().log("You are not logged in.");
            }
        }
    }
}
