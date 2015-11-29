package io.grappl.client.impl;

import io.grappl.client.api.ApplicationMode;
import io.grappl.client.impl.plugin.PluginManager;
import io.grappl.client.impl.stable.commands.CommandHandler;
import io.grappl.client.impl.log.GrapplLog;
import io.grappl.client.impl.log.GrapplErrorStream;
import io.grappl.client.impl.log.SilentGrapplLog;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

/**
 * Represents a single running instance of the Grappl application.
 * Not a tunnel, the entire program.
 *
 * By definition, this class is supposed to represent an entire
 * running instance of the application, and only one can exist in a JVM.
 * Therefore, static fields are acceptable.
 */
public final class Application {

    // TODO: Perhaps have a way to configure a third-party auth server. Or somehow get it to not automatically connect to one on launch (via args?).
    public static final String DOMAIN = "grappl.io";

    // TODO: Maybe come up with... better port numbers? But that will be something done in 2.0.
    public static final int MESSAGING_PORT =   25564;
    public static final int HEARTBEAT =        25570;
    public static final int AUTHENTICATION =   25571;

    public static final String APP_NAME = "Grappl";
    public static final String VERSION = "Beta 1.4.2";

    // If you are distributing your own version, be kind and change this please.
    public static final String BRAND = "DaexsysVanilla";

    public static final String RELAY_LIST_LOC = "http://grappl.io/relays.json";

    public static final String NO_GRAPPL_MESSAGE = "There is no grappl currently open! Start one with 'grappl connect'";
    public static final String NOT_LOGGED_IN_MESSAGE = "Not logged in. Login with 'login [username] [password]' to use this command.";

    public static final long timeStartedRunning = System.currentTimeMillis();

    public static boolean doDetailedLogging = true;
    public static boolean usingSavedHashPass = true;
    public static boolean debugState = true; // For testing using IDE. This is usually enabled in production, too, though.

    private static CommandHandler commandHandler;
    private static GrapplLog log;
    private static ApplicationMode mode;
    private static Image icon;

    private static ApplicationState applicationState;

    public static void main(String[] args) {
        launch("1.0", new String[] {});
    }

    public static void launch(String version, String[] args) {
        log = new GrapplLog();

        log.log("Launcher version: " + version);
        create(args, ApplicationMode.NOGUI);
    }

    public static void create(String[] args, ApplicationMode mode) {
        Application.mode = mode;

        if(log == null)
            log = new GrapplLog();

        log.log("Started: Grappl " + VERSION + " {Brand=" + BRAND + ", Mode=" + mode + "}");
        log.log("If you encounter issues, please report them to @grapplstatus, or @Cactose.");
        log.log("Preferably with a copy of this console! Thx <3");

        log.log("====================");

        getApplicationState();
        commandHandler = new CommandHandler(applicationState);

        System.setErr(new GrapplErrorStream(log, System.out));

        log.log("Preparing to load plugins-");
        PluginManager.setupAndLoad();
        log.log("====================");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
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
            commandHandler = new CommandHandler(applicationState);
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

    public static ApplicationState getApplicationState() {
        if(applicationState == null) {
            applicationState = new ApplicationState();
        }

        return applicationState;
    }

    private static String getStatus(String relay) {
        return checkStatus(relay) ? "online" : "offline";
    }

    private static boolean checkStatus(String relay) {
        try {
            Socket socket = new Socket();
            long start = System.currentTimeMillis();
            socket.connect(new InetSocketAddress(relay, 25564), 2000);
            long ping = (System.currentTimeMillis() - start);
            System.out.println(relay + " " + ping);
        } catch (IOException e) {
            return false;
        }

        return true;
    }
//
//    private static String getOnlineStatus() {
//        return "n=" + getStatus("n.grappl.io")
//                + ", e=" + getStatus("e.grappl.io")
//                + ", s=" + getStatus("s.grappl.io")
//                + ", p=" + getStatus("p.grappl.io")
//                ;
//    }
}
