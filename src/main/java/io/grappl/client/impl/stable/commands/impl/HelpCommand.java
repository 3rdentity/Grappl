package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

public class HelpCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {
        String printedOutput = "Commands: ";

        for(String entries : Application.getCommandHandler().getCommandsAlphabetized()) {
            printedOutput += entries + ", ";
        }

        Application.getLog().log(printedOutput.substring(0, printedOutput.length() - 2));
    }
}
