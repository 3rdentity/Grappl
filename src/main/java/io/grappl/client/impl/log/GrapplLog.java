package io.grappl.client.impl.log;

import io.grappl.client.api.event.ConsoleMessageListener;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.GrapplDataFile;
import io.grappl.client.impl.gui.ConsoleGUI;

import javax.swing.*;
import java.io.*;
import java.text.DateFormat;
import java.util.*;

/**
 * Logging class for Grappl.
 * Eventually start using logging framework?
 */
public class GrapplLog {

    private List<String> loggedMessages = new ArrayList<String>();
    private List<String> visibleLog = new ArrayList<String>();

    private Set<ConsoleMessageListener> consoleMessageListeners = new HashSet<ConsoleMessageListener>();

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

        for(ConsoleMessageListener consoleMessageListener : consoleMessageListeners) {
            consoleMessageListener.receiveMessage(theS);
        }

        try {
            save();
        } catch (Exception e) {}
    }

    public void detailed(String message) {
        if(Application.doDetailedLogging) {
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

            File file = new File(GrapplDataFile.getOSSpecificLocation() + "/logs/" + Application.timeStartedRunning + "-log.log");

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

    public void addConsoleMessageListener(ConsoleMessageListener consoleMessageListener) {
        consoleMessageListeners.add(consoleMessageListener);
    }
}
