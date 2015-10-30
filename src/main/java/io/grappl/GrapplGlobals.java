package io.grappl;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Lots of global variables.
 * Largely port numbers and messages that appear in many places.
 */
public class GrapplGlobals {

    public static final String APP_NAME = "Grappl";
    public static final String DOMAIN = "grappl.io";

    public static final int AUTHENTICATION = 25571;

    public static final int MESSAGING_PORT =   25564;
    public static final int HEARTBEAT =        25570;

    public static final String NO_GRAPPL_MESSAGE = "There is no grappl open!";

    public static final String VERSION = "Beta 1.2";

    public static final long clientTimeOpened = System.currentTimeMillis();
    public static boolean doDetailedLogging = true;
    public static boolean usingSavedHashPass = true;
    public static boolean debugState = true;
    private static Image icon;

    public static Image getIcon() {
        if(icon == null) {
            try {
                icon = Toolkit.getDefaultToolkit().getImage(new URL("http://grappl" +
                        ".io:888/html/glogo.png"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return icon;
    }
}
