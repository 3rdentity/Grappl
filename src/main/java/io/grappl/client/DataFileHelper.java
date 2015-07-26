package io.grappl.client;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

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
