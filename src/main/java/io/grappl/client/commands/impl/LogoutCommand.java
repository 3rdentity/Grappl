package io.grappl.client.commands.impl;

import io.grappl.GrapplGlobals;
import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

public class LogoutCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {

        if(grappl == null) {
            Application.getClientLog().log(GrapplGlobals.NO_GRAPPL_MESSAGE);
        } else {
            grappl.getAuthentication().logout();
            Application.getClientLog().log("Logged out");
        }
    }
}
