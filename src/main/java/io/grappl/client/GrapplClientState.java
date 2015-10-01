package io.grappl.client;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

public class GrapplClientState {
    public static final String VERSION = "Beta 1.1";
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
