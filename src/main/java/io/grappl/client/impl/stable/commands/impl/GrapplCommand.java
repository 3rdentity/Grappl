package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.api.Protocol;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.error.RelayServerNotFoundException;
import io.grappl.client.impl.log.GrapplLog;
import io.grappl.client.impl.relay.adaptive.AdaptiveConnector;
import io.grappl.client.impl.stable.GrapplBuilder;
import io.grappl.client.impl.stable.StatMonitor;

public class GrapplCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {

        if(args.length > 1) {
            final String subCommand = args[1];

            if (subCommand.equalsIgnoreCase("connect")) {
                // Default relay is the NYC one.
                String relay = "unset";

                if (args.length == 3) {
                    relay = args[2];
                }

                Grappl grappl = new GrapplBuilder(state, Protocol.TCP).build();
                state.addGrappl(grappl);

                try {
                    if(relay.equals("unset")) {
                        AdaptiveConnector adaptiveConnector = new AdaptiveConnector(state.getRelayManager());
                        adaptiveConnector.subject(grappl);
                        relay = grappl.getExternalServer().getAddress();
                    } else {
                        grappl.connect(relay);
                    }

                    Application.getLog().log("Connected to relay server @ " + relay);
                } catch (RelayServerNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                if (state != null && state.getFocusedGrappl() != null) {
                    Grappl grappl = state.getFocusedGrappl();
                    GrapplLog log = Application.getLog();

                    if (subCommand.equalsIgnoreCase("disconnect")) {
                        grappl.disconnect();
                        Application.getLog().log("Disconnected");
                    } else if (subCommand.equalsIgnoreCase("state")) {
                        log.log(grappl.getExternalServer().getAddress() + " relay server");
                        log.log(grappl.getExternalServer().getPort() + " external port");
                        log.log(grappl.getInternalServer().getAddress() + " internal server");
                        log.log(grappl.getInternalServer().getPort() + " internal port");
                    } else if (subCommand.equalsIgnoreCase("relay") || subCommand.equalsIgnoreCase("externaladdress")) {
                        log.log("Relay: " + grappl.getExternalServer().getAddress());
                    } else if (subCommand.equalsIgnoreCase("externalport")) {
                        log.log("External port: " + grappl.getExternalServer().getPort());
                    } else if (subCommand.equalsIgnoreCase("internaladdress")) {
                        grappl.getInternalServer().setAddress(args[2]);
                        log.log("Internal address set to: " + args[2]);
                    } else if (subCommand.equalsIgnoreCase("internalport")) {
                        grappl.getInternalServer().setPort(Integer.parseInt(args[2]));
                        log.log("Internal port set to: " + args[2]);
                    } else if (subCommand.equalsIgnoreCase("stats")) {

                        StatMonitor statsManager = state.getFocusedGrappl().getStatMonitor();

                        Application.getLog().log("== CURRENT CONNECTION STATISTICS ====");
                        Application.getLog().log(statsManager.getTotalConnections() + " clients connected");
                        Application.getLog().log(statsManager.getReceivedData() + " bytes received");
                        Application.getLog().log(statsManager.getSentData() + " bytes sent");
                        Application.getLog().log("=====================================");
                    } else if (subCommand.equalsIgnoreCase("resetstats")) {
                        grappl.getStatMonitor().reset();

                        log.log("Stats reset");
                    }

                    else if(subCommand.equalsIgnoreCase("setfocus")) {
                        int newFocus = Integer.parseInt(args[2]);
                        state.setFocusedIndex(newFocus);
                        log.log("Set focus to: " + newFocus);
                    }

                    else if(subCommand.equalsIgnoreCase("focus")) {
                        log.log("Current focus: " + state.getFocusedIndex());
                    }

                    else {
                        log.log("Unknown subcommand '" + subCommand + "'");
                    }

                } else {
                    Application.getLog().log(Application.NO_GRAPPL_MESSAGE);
                }
            }
        } else {
            Application.getLog().log("You must give a subcommand!");
        }
    }

    @Override
    public String getDescription() {
        return "Allows the user to control the currently focused Grappl instance.\n" +
                "Subcommands: {connect, disconnect, state, relay, externalport, internaladdress, internalport, stats, resetstats, focus, setfocus}";
    }
}
