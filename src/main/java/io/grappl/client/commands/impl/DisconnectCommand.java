package io.grappl.client.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

public class DisconnectCommand implements Command {

    public void runCommand(Grappl grappl, String[] args) {
        if(grappl == null) {
            Application.getClientLog().log("Not connected, cannot disconnect");
        } else {
            Application.getClientLog().log("Disconnecting...");
            grappl.disconnect();
        }
    }
}
