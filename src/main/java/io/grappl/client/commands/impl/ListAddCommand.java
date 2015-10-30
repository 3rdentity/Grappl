package io.grappl.client.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

import java.io.PrintStream;

public class ListAddCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {

        try {
            ClientLog.log("Adding to server list");

            grappl.getAuthentication().getAuthDataOutputStream().writeByte(6);
            String game = args[1];
            PrintStream printStream = new PrintStream(grappl.getAuthentication().getAuthDataOutputStream());

            Application.getCommandHandler().returnBuffer =
                    game + " - " + grappl.getRelayServer() + ":" + grappl.getExternalPort();

            printStream.println(Application.getCommandHandler().returnBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
