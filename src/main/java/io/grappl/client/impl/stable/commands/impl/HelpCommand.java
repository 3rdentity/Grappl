package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

public class HelpCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {

        if(args.length > 1) {
            final String specifiedCommand = args[1];

            if(Application.getCommandHandler().hasCommand(specifiedCommand)) {
                Command inQuestion = Application.getCommandHandler().getCommand(specifiedCommand);

                Application.getLog().log("Help for command: " + specifiedCommand);
                Application.getLog().log(inQuestion.getDescription());
            } else {
                Application.getLog().log("Unknown command to fetch help for: " + specifiedCommand);
            }
        } else {
            String printedOutput = "Commands (" + Application.getCommandHandler().getCommandsAlphabetized().size() + " total): \n";

            for(String entries : Application.getCommandHandler().getCommandsAlphabetized()) {
                printedOutput += entries + ", ";
            }

            Application.getLog().log(printedOutput.substring(0, printedOutput.length() - 2));
        }
    }

    @Override
    public String getDescription() {
        return "Lists help information about commands.";
    }
}
