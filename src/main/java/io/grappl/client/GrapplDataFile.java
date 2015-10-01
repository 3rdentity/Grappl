package io.grappl.client;

import java.io.*;

/**
 * Interface to save and load data in user.dat.
 */
public class GrapplDataFile {
    public static String getUsername() {
        File file = new File(getOSSpecificLocation() + "/user.dat");
        try {
            file.createNewFile();
        } catch (IOException e) {
        }

        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            return dataInputStream.readLine();
        } catch (Exception e) {
        }

        return "";
    }

    public static String getPassword() {
        File file = new File(getOSSpecificLocation() + "/user.dat");

        new File(getOSSpecificLocation()).mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
        }

        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            dataInputStream.readLine();
            return dataInputStream.readLine();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void saveUsername(String username, char[] password) {
        File file = new File(getOSSpecificLocation() + "/user.dat");

        file.mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            PrintStream printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(username);

            if(password != null) {
                printStream.println(password);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getOSSpecificLocation() {
        final String os = System.getProperty("os.name").toLowerCase();

        if(os.contains("win")) {
            return System.getenv("APPDATA") + "/Daexsys/Grappl/";
        } else if(os.contains("mac")) {
            return System.getProperty("user.home") + "/Library/Application Support/Grappl/";
        } else { // Linux / BSD
            return System.getProperty("user.home") + "/Grappl/";
        }
    }
}
