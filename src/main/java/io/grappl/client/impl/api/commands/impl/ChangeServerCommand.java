package io.grappl.client.impl.api.commands.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;

public class ChangeServerCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {
        String server = args[1];
        grappl.setInternalAddress(server);
        Application.getLog().log("Server is now " + server);
    }
}
