package io.grappl.client.commands.impl;

import io.grappl.GrapplGlobals;
import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

public class VersionCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {
        Application.getClientLog().log(GrapplGlobals.APP_NAME + " version " + GrapplGlobals.VERSION);
    }
}
