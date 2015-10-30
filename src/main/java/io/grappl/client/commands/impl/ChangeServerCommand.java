package io.grappl.client.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

public class ChangeServerCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {
        String server = args[1];
        grappl.setInternalAddress(server);
        Application.getClientLog().log("Server is now " + server);
    }
}
