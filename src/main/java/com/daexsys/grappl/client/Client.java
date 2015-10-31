package com.daexsys.grappl.client;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.GrapplBuilder;
import io.grappl.client.gui.StandardGUI;

/**
 * Class that hosts the main method called when Grappl is started.
 */
public class Client {

    public static void main(String[] args) {

        Application.create(args);

        boolean displayGui = true;

        // Handle command line arguments
        if(args.length > 1) {
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
                grappl.connect("n.grappl.io");
            }
        }

        // If there should be a gui, create it
        if(displayGui) {
            new StandardGUI();
        }
    }
}
