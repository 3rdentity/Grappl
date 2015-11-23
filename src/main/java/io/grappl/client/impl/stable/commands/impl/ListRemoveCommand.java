package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

import java.io.PrintStream;

public class ListRemoveCommand implements Command {
    @Override
    public void runCommand(ApplicationState state, String[] args) {

        try {
            Application.getLog().log("Removing from server list");
            state.getAuthentication().getAuthDataOutputStream().writeByte(7);
            PrintStream printStream = new PrintStream(state.getAuthentication().getAuthDataOutputStream());
            printStream.println("asd");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
