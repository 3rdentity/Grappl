package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;
import io.grappl.client.commands.CommandHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Map;

public class HelpCommand implements Command{
    @Override
    public void runCommand(Grappl grappl, String[] args, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        String printedOutput = "Commands: ";

        for(Map.Entry<String, Command> entries : CommandHandler.commandMap.entrySet()) {
            printedOutput += entries.getKey() + ", ";
        }

        ClientLog.log(printedOutput.substring(0, printedOutput.length() - 2));

    }
}
