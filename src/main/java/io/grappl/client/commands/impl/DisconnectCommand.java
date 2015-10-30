package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

public class DisconnectCommand implements Command {

    public void runCommand(Grappl grappl, String[] args) {
        if(grappl == null) {
            ClientLog.log("Not connected, cannot disconnect");
        } else {
            ClientLog.log("Disconnecting...");
            grappl.disconnect();
        }
    }
}
