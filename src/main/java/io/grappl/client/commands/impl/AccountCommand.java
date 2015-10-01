package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;

public class AccountCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {

        final String subCommand = args[1];

        if(subCommand.equalsIgnoreCase("donate")) {
            try {
                Desktop.getDesktop().browse(URI.create("http://grappl.io/donate"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        else if(subCommand.equalsIgnoreCase("info")) {
            ClientLog.log(grappl.getUsername());
        }

        // Need to redo to actually fetch static port related info.
        else if(subCommand.equalsIgnoreCase("staticports")) {
            ClientLog.log(grappl.getExternalPort());
        }
    }
}