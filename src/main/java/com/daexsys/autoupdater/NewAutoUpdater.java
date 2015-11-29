package com.daexsys.autoupdater;

import io.grappl.client.impl.Application;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

@SuppressWarnings("All")
public class NewAutoUpdater {

    public static final String APP_NAME = "Grappl";

    public static void main(String[] args) {
        String localURL = getOSSpecificLocation();

        File file = new File(localURL);
        file.mkdirs();

        localURL += "/GrapplClient.jar";

        downloadJar("http://grappl.io:888/html/GrapplClient.jar", localURL);

        try {
            URLClassLoader loader =
                    new URLClassLoader(new URL[]{ new File(localURL).toURI().toURL() });

            JarInputStream jarInputStream = new JarInputStream(new FileInputStream(localURL));

            java.util.List<String> classes = new ArrayList<String>();

            while (true) {
                JarEntry jarEntry = jarInputStream.getNextJarEntry();

                if (jarEntry == null) {
                    break;
                } else {
                    if (jarEntry.getName().endsWith(".class")) {
                        String entry = jarEntry.getName().replaceAll("\\/", "\\.");

                        String[] spl = entry.split("\\.");
                        String build = spl[0];

                        for (int i = 1; i < spl.length; i++) {
                            if (i == spl.length - 1) ;
                            else build += "." + spl[i];
                        }

                        classes.add(build);
                    }
                }
            }

            Map<String, Class> classMap = new HashMap<String, Class>();

            for (String s : classes) {
                Class<?> initiator = loader.loadClass(s);
                classMap.put(s, initiator);
            }

            classMap.get("com.daexsys.grappl.client.Client")
                    .getMethod("main", String[].class).invoke(null, (Object) new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadJar(String url, String localURL) {
        File localFile = new File(localURL);

        try {
            localFile.createNewFile();

            BufferedInputStream stream = new BufferedInputStream(new URL(url).openStream());
            BufferedOutputStream ostream = new BufferedOutputStream(new FileOutputStream(localURL));

            byte[] stuff = new byte[1024];

            int amount = 0;
            while((amount = stream.read(stuff,0,1024)) != -1) {
                ostream.write(stuff, 0, amount);
            }

            stream.close();
            ostream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getOSSpecificLocation() {
        final String os = System.getProperty("os.name").toLowerCase();

        if(os.contains("win")) {
            return System.getenv("APPDATA") + "/Daexsys/ "
                    + APP_NAME + "/";

        } else if(os.contains("mac")) {
            return System.getProperty("user.home") + "/Library/Application Support/"
                    + APP_NAME + "/";

        } else { // Linux / BSD
            return System.getProperty("user.home") + "/ "
                    + APP_NAME + "/";
        }
    }
}
