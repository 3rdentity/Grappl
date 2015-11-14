package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class AccountCommand implements Command {
    @Override
    public void runCommand(ApplicationState state, String[] args) {

        final String subCommand = args[1];

        if(subCommand.equalsIgnoreCase("donate")) {
            try {
                Desktop.getDesktop().browse(URI.create("http://grappl.io/donate"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        else if(subCommand.equalsIgnoreCase("info")) {
            Application.getLog().log(state.getAuthentication().getUsername());
        }

        // Need to redo to actually fetch static port related info.
        else if(subCommand.equalsIgnoreCase("staticports")) {
            Application.getLog().log(state.getFocusedGrappl().getExternalServer().getPort() + "");
        }
    }
}
