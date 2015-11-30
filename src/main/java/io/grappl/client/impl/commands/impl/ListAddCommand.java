package io.grappl.client.impl.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

import java.io.PrintStream;

public class ListAddCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {

        try {
            Application.getLog().log("Adding to server list");

            state.getAuthentication().getAuthDataOutputStream().writeByte(6);
            String game = args[1];
            PrintStream printStream = new PrintStream(state.getAuthentication().getAuthDataOutputStream());
//
//            Application.getCommandHandler().returnBuffer =
//                    game + " - " + state.getFocusedGrappl().getExternalServer().getAddress() + ":"
//                            + state.getFocusedGrappl().getExternalServer().getPort();
//
//            printStream.println(Application.getCommandHandler().returnBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDescription() {
        return "Adds a server to the global server list";
    }
}
