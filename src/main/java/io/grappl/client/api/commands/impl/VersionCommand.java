package io.grappl.client.api.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.commands.Command;

public class VersionCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {
        Application.getLog().log(Application.APP_NAME + " version " + Application.VERSION);
    }
}
