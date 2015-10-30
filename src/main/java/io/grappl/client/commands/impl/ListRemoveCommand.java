package io.grappl.client.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

import java.io.PrintStream;

public class ListRemoveCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {
        try {
            Application.getClientLog().log("Removing from server list");
            grappl.getAuthentication().getAuthDataOutputStream().writeByte(7);
            PrintStream printStream = new PrintStream(grappl.getAuthentication().getAuthDataOutputStream());
            printStream.println(Application.getCommandHandler().returnBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
