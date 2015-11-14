package io.grappl.client.impl.api.commands.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;

import java.io.PrintStream;

public class ListRemoveCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {

        try {
            Application.getLog().log("Removing from server list");
            grappl.getAuthentication().getAuthDataOutputStream().writeByte(7);
            PrintStream printStream = new PrintStream(grappl.getAuthentication().getAuthDataOutputStream());
            printStream.println(Application.getCommandHandler().returnBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
