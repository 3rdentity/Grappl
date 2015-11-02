package io.grappl.client.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

import java.util.Map;

public class HelpCommand implements Command{
    @Override
    public void runCommand(Grappl grappl, String[] args) {
        String printedOutput = "Commands: ";

        for(String entries : Application.getCommandHandler().getCommandsAlphabetized()) {
            printedOutput += entries + ", ";
        }

        Application.getClientLog().log(printedOutput.substring(0, printedOutput.length() - 2));
    }
}
