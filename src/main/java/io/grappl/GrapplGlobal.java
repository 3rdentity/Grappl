package io.grappl;

import io.grappl.client.api.Grappl;

/**
 * Lots of global variables
 */
public class GrapplGlobal {
    public static final String APP_NAME = "Grappl";
    public static final String DOMAIN = "grappl.io";

    public static final int INNER_TRANSIT =  2178;
    public static final int RELAY_CONTROL =  25563;
    public static final int MESSAGING_PORT =   25564;
    public static final int HEARTBEAT =      25570;
    public static final int AUTHENTICATION = 25571;

    public static final String NO_GRAPPL_MESSAGE = "There is no grappl open!";

    public static Grappl theGrappl;
}
