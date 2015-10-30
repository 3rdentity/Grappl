package io.grappl.client.commands.impl;

import io.grappl.GrapplGlobals;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

public class LogoutCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {

        if(grappl == null) {
            ClientLog.log(GrapplGlobals.NO_GRAPPL_MESSAGE);
        } else {
            grappl.getAuthentication().logout();
            ClientLog.log("Logged out");
        }
    }
}
