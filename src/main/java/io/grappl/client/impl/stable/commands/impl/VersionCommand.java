package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

public class VersionCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {
        Application.getLog().log(Application.APP_NAME + " " + Application.VERSION + " {Brand=" + Application.BRAND + "}");
    }

    @Override
    public String getDescription() {
        return "Displays the software version in the console.";
    }
}
