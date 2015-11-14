package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;

import java.io.PrintStream;

public class ListAddCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {

        try {
            Application.getLog().log("Adding to server list");

            grappl.getAuthentication().getAuthDataOutputStream().writeByte(6);
            String game = args[1];
            PrintStream printStream = new PrintStream(grappl.getAuthentication().getAuthDataOutputStream());

            Application.getCommandHandler().returnBuffer =
                    game + " - " + grappl.getExternalServer().getAddress() + ":" + grappl.getExternalServer().getPort();

            printStream.println(Application.getCommandHandler().returnBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
