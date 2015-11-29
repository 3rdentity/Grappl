package io.grappl.client.impl;

import java.io.*;

/**
 * Interface to save and load data in user.dat.
 *
 * Can get away with being full of static methods since there is only one data file!
 * The class also carries no state (other than user.dat :P).
 */
// TODO: Overhaul. This. So. Much.
// TODO: A weird structureless format? Really. Obvsly stable. Oh, and don't forget that this doesn't even work 100% properly on OSX and linux.
public class GrapplDataFile {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String getUsername() {
        File file = new File(getOSSpecificLocation() + "/user.dat");
        try {
            file.createNewFile();
        } catch (IOException ignore) {}

        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            return dataInputStream.readLine();
        } catch (Exception ignore) {}

        return "ErrorPerson";
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String getPassword() {
        File file = new File(getOSSpecificLocation() + "/user.dat");

        new File(getOSSpecificLocation()).mkdirs();

        try {
            file.createNewFile();
        } catch (IOException ignore) {}

        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            dataInputStream.readLine();
            return dataInputStream.readLine();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
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
            return System.getenv("APPDATA") + "/Daexsys/ "
                    + Application.APP_NAME + "/";

        } else if(os.contains("mac")) {
            return System.getProperty("user.home") + "/Library/Application Support/"
                    + Application.APP_NAME + "/";

        } else { // Linux / BSD
            return System.getProperty("user.home") + "/ "
                    + Application.APP_NAME + "/";
        }
    }
}
