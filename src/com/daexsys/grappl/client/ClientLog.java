package com.daexsys.grappl.client;

import javax.swing.*;
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
    }

    public static List<String> getTheLog() {
        return theLog;
    }
}
