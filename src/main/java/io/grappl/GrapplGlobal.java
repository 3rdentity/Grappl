package io.grappl;

public class GrapplGlobal {
    public static String APP_NAME = "Grappl";
//    public static String DOMAIN = "localhost";
    public static String DOMAIN = "grappl.io";

    // Original ports
    public static int INNER_TRANSIT = 2178;
    public static int MESSAGE_PORT = 25564;
    public static int HEARTBEAT = 25570;
    public static int RELAY_CONTROL = 25563;

    // New ports
    public static int AUTHENTICATION = 25571;
}
