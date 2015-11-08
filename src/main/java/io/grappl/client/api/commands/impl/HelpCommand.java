package io.grappl.client.api.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.commands.Command;

public class HelpCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        String printedOutput = "Commands: ";

        for(String entries : Application.getCommandHandler().getCommandsAlphabetized()) {
            printedOutput += entries + ", ";
        }

        Application.getLog().log(printedOutput.substring(0, printedOutput.length() - 2));
    }
}
