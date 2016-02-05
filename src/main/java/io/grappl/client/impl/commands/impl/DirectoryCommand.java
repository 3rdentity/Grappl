package io.grappl.client.impl.commands.impl;

import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.GrapplDataFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class DirectoryCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {
        final String location = GrapplDataFile.getOSSpecificLocation();

        try {
            Desktop.getDesktop().open(new File(location));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Application.getLog().log("Opened: " + location);
    }

    @Override
    public String getDescription() {
        return "Open's Grappl's data directory in your file explorer";
    }
}
