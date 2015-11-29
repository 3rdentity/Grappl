package com.daexsys.grappl.client;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.api.ApplicationMode;
import io.grappl.client.impl.relay.AdaptiveConnector;
import io.grappl.client.impl.stable.GrapplBuilder;
import io.grappl.client.impl.gui.DefaultGUI;
import io.grappl.client.impl.error.RelayServerNotFoundException;

/**
 * Class that hosts the main method called when Grappl is started.
 */
public class Client {

    public static void main(String[] args) {

//        args = new String[]{ "-nogui" };

        Application.create(args, ApplicationMode.GUI);

        boolean displayGui = true;

        // Handle command line arguments
        if(args.length > 0) {
            Application.setMode(ApplicationMode.NOGUI);

            if (args[0].equalsIgnoreCase("-nogui") || args[0].equalsIgnoreCase("nogui")) {
                displayGui = false;
            }

            /*
                 If command used to start Grappl is of the format: java -jar Grappl.jar nogui #####
                 Connect immediately to n.grappl.io and relay from local server @ #####.
                 For convenience in bash scripts and such.
              */
            if(args.length == 2) {
                int localPort = Integer.parseInt(args[1]);

                GrapplBuilder grapplBuilder = new GrapplBuilder();
                grapplBuilder.atLocalPort(localPort);
                Grappl grappl = grapplBuilder.build();

                AdaptiveConnector adaptiveConnector =
                        new AdaptiveConnector(Application.getApplicationState().getRelayManager());

                adaptiveConnector.subject(grappl);

            } else {
                Application.getCommandHandler().createConsoleCommandListenThread();
            }
        }

        Application.getCommandHandler().createConsoleCommandListenThread();

        // If there should be a gui, create it
        if(displayGui) {
            new DefaultGUI(Application.getApplicationState());
        }
    }
}
