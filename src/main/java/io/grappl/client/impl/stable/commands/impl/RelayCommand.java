package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;

public class RelayCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {
        if(grappl == null) {
            Application.getLog().log("No tunnel established, no relay");
        } else {
            Application.getLog().log("Current relay: " + grappl.getExternalServer().getAddress());
        }
    }
}
