package io.grappl.client.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

public class RelayCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {
        if(grappl == null) {
            Application.getClientLog().log("No tunnel established, no relay");
        } else {
            Application.getClientLog().log("Current relay: " + grappl.getRelayServer());
        }
    }
}
