package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.log.GrapplLog;
import io.grappl.client.impl.stable.Authentication;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

@SuppressWarnings("SpellCheckingInspection")
public class AccountCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {

        GrapplLog log = Application.getLog();
        Authentication authentication = state.getAuthentication();

        if(authentication != null) {
            if (args.length > 1) {
                final String subCommand = args[1];

                if (subCommand.equalsIgnoreCase("donate")) {
                    try {
                        Desktop.getDesktop().browse(URI.create("http://grappl.io/donate"));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else if (subCommand.equalsIgnoreCase("username")) {
                    log.log("Username: " + authentication.getUsername());
                } else if (subCommand.equalsIgnoreCase("isalpha")) {
                    log.log("Alpha: " + authentication.isPremium());
                } else if (subCommand.equalsIgnoreCase("staticports")) {
                    log.log(state.getFocusedGrappl().getExternalServer().getPort() + "");
                }
            } else {
                log.log("Subcommand required. Options: {donate, username, isalpha, staticports}");
            }
        } else {
            log.log(Application.NOT_LOGGED_IN_MESSAGE);
        }
    }

    @Override
    public String getDescription() {
        return "Find out account info and control account variables";
    }
}
