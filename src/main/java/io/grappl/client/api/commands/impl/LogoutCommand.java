package io.grappl.client.api.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.commands.Command;

public class LogoutCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {

        if(grappl == null) {
            Application.getLog().log(Application.NO_GRAPPL_MESSAGE);
        } else {
            grappl.getAuthentication().logout();
            Application.getLog().log("Logged out");
        }
    }
}
