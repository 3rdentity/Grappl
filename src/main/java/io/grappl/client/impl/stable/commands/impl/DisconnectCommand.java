package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;

public class DisconnectCommand implements Command {

    public void runCommand(Grappl grappl, String[] args) {
        if(grappl == null) {
            Application.getLog().log("Not connected, cannot disconnect");
        } else {
            Application.getLog().log("Disconnecting...");
            grappl.disconnect();
        }
    }
}
