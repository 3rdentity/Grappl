package io.grappl.client.impl;

import io.grappl.client.impl.api.commands.CommandHandler;
import io.grappl.client.impl.log.GrapplLog;
import io.grappl.client.impl.log.GrapplErrorStream;
import io.grappl.client.impl.log.SilentGrapplLog;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Represents a single running instance of the Grappl application.
 * Not a tunnel, the entire program.
 *
 * By definition, this class is supposed to represent an entire
 * running instance of the application, and only one can exist in a JVM.
 * Therefore, static fields are acceptable.
 */
public final class Application {

    public static final String DOMAIN = "grappl.io";
    public static final int AUTHENTICATION = 25571;
    public static final int MESSAGING_PORT =   25564;
    public static final int HEARTBEAT =        25570;
    public static final String VERSION = "Beta 1.3";
    public static final String NO_GRAPPL_MESSAGE = "There is no grappl currently open! Start one with /init";

    private static List<String> nycOrder =     Arrays.asList("n.grappl.io", "s.grappl.io", "e.grappl.io", "p.grappl.io");
    private static List<String> sFOrder =      Arrays.asList("s.grappl.io", "n.grappl.io", "e.grappl.io", "p.grappl.io");
    private static List<String> europeOrder =  Arrays.asList("e.grappl.io", "n.grappl.io", "p.grappl.io", "s.grappl.io");
    private static List<String> pacificOrder = Arrays.asList("p.grappl.io", "s.grappl.io", "n.grappl.io", "e.grappl.io");

    public static final long timeStartedRunning = System.currentTimeMillis();
    public static final String APP_NAME = "Grappl";

    public static boolean doDetailedLogging = true;
    public static boolean usingSavedHashPass = true;
    public static boolean debugState = true;

    private static CommandHandler commandHandler;
    private static GrapplLog log;
    private static ApplicationMode mode;
    private static Image icon;

    public static void create(String[] args, ApplicationMode mode) {
        Application.mode = mode;
        log = new GrapplLog();
        commandHandler = new CommandHandler();

        System.setErr(new GrapplErrorStream(log, System.out));
    }

    public static void setMode(ApplicationMode mode) {
        Application.mode = mode;
    }

    public static ApplicationMode getApplicationMode() {
        return mode;
    }

    public static GrapplLog getLog() {
        if(log == null) {
            log = new SilentGrapplLog();
        }

        return log;
    }

    public static CommandHandler getCommandHandler() {
        if(commandHandler == null) {
            commandHandler = new CommandHandler();
        }

        return commandHandler;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static Image getIcon() {
        if(icon == null) {
            try {
                icon = Toolkit.getDefaultToolkit().getImage(new URL("http://grappl" +
                        ".io:888/html/glogo.png"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return icon;
    }

    public static List<String> getNewYorkOrder() {
        return nycOrder;
    }

    public static List<String> getSFBayOrder() {
        return sFOrder;
    }

    public static List<String> getEuropeOrder() {
        return europeOrder;
    }

    public static List<String> getPacificOrder() {
        return pacificOrder;
    }
}
