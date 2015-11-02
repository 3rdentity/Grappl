package io.grappl.client.log;

import io.grappl.GrapplGlobals;
import io.grappl.client.GrapplDataFile;
import io.grappl.client.gui.ConsoleGUI;

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

    private List<String> loggedMessages = new ArrayList<String>();
    private List<String> visibleLog = new ArrayList<String>();

    public void log(String message) {
        String tag = DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
        String theS = "[" + tag + "] " + message;

        if(!message.equals("PING")) {
            System.out.println(theS);
            loggedMessages.add(theS);
            visibleLog.add(theS);
        }

        try {
            if (ConsoleGUI.logDisplay != null) {
                JTextArea display = ConsoleGUI.logDisplay;
                display.setText(null);

                List<String> log = visibleLog;
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
            loggedMessages.add(theS);
            visibleLog.add(theS);

            if (ConsoleGUI.logDisplay != null) {
                JTextArea display = ConsoleGUI.logDisplay;
                display.setText(null);

                List<String> log = visibleLog;
                for (String s : log) {
                    display.append(s + "\n");
                }
            }

            save();
        }
    }

    public void clearVisibleLog() {
        visibleLog.clear();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void save() {
        try {
            File dirs = new File(GrapplDataFile.getOSSpecificLocation() + "/logs/");
            dirs.mkdirs();

            File file = new File(GrapplDataFile.getOSSpecificLocation() + "/logs/" + GrapplGlobals.clientTimeOpened + "-log.log");

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            PrintStream printStream = new PrintStream(fileOutputStream);

            for(String s : loggedMessages) {
                printStream.println(s);
            }
            printStream.close();
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException ignore) {}
    }

    public List<String> getLoggedMessages() {
        return loggedMessages;
    }
}
