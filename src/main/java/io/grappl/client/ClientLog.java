package io.grappl.client;

import io.grappl.GrapplGlobals;
import io.grappl.client.gui.ConsoleWindow;

import javax.swing.*;
import java.io.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Logging class for Grappl.
 * Eventually start using logging framework?
 */
public class ClientLog {
    private List<String> theLog = new ArrayList<String>();

    public static void log(String message) {
        Application.getClientLog().plog(message);
    }

    public void plog(String message) {
        String tag = DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
        String theS = "[" + tag + "] " + message;
        System.out.println(theS);
        theLog.add(theS);

        try {
            if (ConsoleWindow.display != null) {
                JTextArea display = ConsoleWindow.display;
                display.setText(null);

                List<String> log = this.getTheLog();
                for (String s : log) {
                    display.append(s + "\n");
                }
            }
        } catch (Exception ignore) {}

        save();
    }

    public void detailed(String message) {
        if(GrapplGlobals.doDetailedLogging) {
            String tag = DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
            String theS = "[" + tag + "] " + message;
            System.out.println(theS);
            theLog.add(theS);

            if (ConsoleWindow.display != null) {
                JTextArea display = ConsoleWindow.display;
                display.setText(null);

                List<String> log = this.getTheLog();
                for (String s : log) {
                    display.append(s + "\n");
                }
            }

            save();
        }
    }

    public void save() {
        try {
            File dirs = new File(GrapplDataFile.getOSSpecificLocation());
            dirs.mkdirs();

            File file = new File(GrapplDataFile.getOSSpecificLocation() + GrapplGlobals.clientTimeOpened + "-log.log");
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
//            e.printStackTrace();
        }
    }

    public List<String> getTheLog() {
        return theLog;
    }
}
