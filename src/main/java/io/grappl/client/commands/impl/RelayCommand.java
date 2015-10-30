package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

public class RelayCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {
        if(grappl == null) {
            ClientLog.log("No tunnel established, no relay");
        } else {
            ClientLog.log("Current relay: " + grappl.getRelayServer());
        }
    }
}
