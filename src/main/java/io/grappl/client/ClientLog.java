package io.grappl.client;

import io.grappl.client.gui.ConsoleWindow;

import javax.swing.*;
import java.io.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientLog {
    public static List<String> theLog = new ArrayList<String>();

    public static void log(String toBeLogged) {
        String tag = DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
        String theS = "[" + tag + "] " + toBeLogged;
        System.out.println(theS);
        theLog.add(theS);

        if(ConsoleWindow.display != null) {
            JTextArea display = ConsoleWindow.display;
            display.setText(null);

            List<String> log = ClientLog.getTheLog();
            for(String s : log) {
                display.append(s + "\n");
            }
        }

        save();
    }

    public static void detailed(String toBeLogged) {
        if(GrapplClientState.doDetailedLogging) {
            String tag = DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
            String theS = "[" + tag + "] " + toBeLogged;
            System.out.println(theS);
            theLog.add(theS);

            if (ConsoleWindow.display != null) {
                JTextArea display = ConsoleWindow.display;
                display.setText(null);

                List<String> log = ClientLog.getTheLog();
                for (String s : log) {
                    display.append(s + "\n");
                }
            }

            save();
        }
    }

    public static void save() {
        try {
            File dirs = new File(GrapplDataFile.getOSSpecificLocation());
            dirs.mkdirs();

            File file = new File(GrapplDataFile.getOSSpecificLocation() + GrapplClientState.clientTimeOpened + "-log.log");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            PrintStream printStream = new PrintStream(fileOutputStream);

            for(String s : theLog) {
                printStream.println(s);
            }
            printStream.close();
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getTheLog() {
        return theLog;
    }
}
