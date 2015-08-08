package io.grappl.client;

public class DataFileHelper {
    public static String getOSSpecificLocation() {
        final String os = System.getProperty("os.name").toLowerCase();

        if(os.contains("win")) {
            return System.getenv("APPDATA") + "/Daexsys/Grappl/";
        } else if(os.contains("mac")) {
            return System.getenv("user.home") + "/Library/Application Support/Grappl/";
        } else { // Linux or non-Mac BSD
            return System.getProperty("user.home") + "/Grappl/";
        }
    }
}
