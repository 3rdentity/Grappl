package io.grappl.client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

public class GrapplClientState {
    public static final String VERSION = "1.4 Alpha";
    public static final long clientTimeOpened = System.currentTimeMillis();

    public static boolean doDetailedLogging = true;
    public static boolean audible = false;

    public static boolean usingSavedHashPass = false;

    public static Image icon;

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
