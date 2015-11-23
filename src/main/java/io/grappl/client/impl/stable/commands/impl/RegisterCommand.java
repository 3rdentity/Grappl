package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class RegisterCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {
        try {
            Desktop.getDesktop().browse(URI.create("http://grappl.io/register"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public String getDescription() {
        return "Brings a user to the web register page.";
    }
}
