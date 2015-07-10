package com.daexsys.grappl.client;

import java.io.*;

public class GrapplDataFile {
    public static String getUsername() {
        File file = new File(getOSSpecificLocation() + "/user.dat");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            return dataInputStream.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void saveUsername(String username) {
        File file = new File(getOSSpecificLocation() + "/user.dat");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            PrintStream printStream = new PrintStream(new FileOutputStream(file));
            printStream.println(username);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getOSSpecificLocation() {
        final String os = System.getProperty("os.name").toLowerCase();

        if(os.contains("win")) {
//            System.out.println("win");
            return System.getenv("APPDATA") + "/Daexsys/Grappl/";
        } else if(os.contains("mac")) {
//            System.out.println("mac");
            return System.getenv("user.home") + "/Library/Application Support/Grappl/";
        } else { // Linux or non-Mac BSD
//            System.out.println("other");
            return System.getProperty("user.home") + "/Grappl/";
        }
    }
}
