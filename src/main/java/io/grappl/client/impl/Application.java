package io.grappl.client.impl;

import io.grappl.client.api.ApplicationMode;
import io.grappl.client.impl.plugin.PluginManager;
import io.grappl.client.impl.commands.CommandHandler;
import io.grappl.client.impl.log.GrapplLog;
import io.grappl.client.impl.log.GrapplErrorStream;
import io.grappl.client.impl.log.SilentGrapplLog;
import io.grappl.gui.DefaultGUI;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
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

    public static final String DOMAIN = "grappl.io";

    // TODO: Maybe come up with... better port numbers? But that will be something done in 2.0.
    public static final int MESSAGING_PORT =   25564;
    public static final int HEARTBEAT =        25570;
    public static final int AUTHENTICATION =   25571;

    public static final String APP_NAME = "Grappl";
    public static final String VERSION = "Beta 1.6.5";

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
        try {
            log = new GrapplLog();

            log.log("Launcher version: " + version);
            create(args, ApplicationMode.GUI);
        } catch(Exception e) {
            JOptionPane.showConfirmDialog(null, "Last resort error catch. Error: " + e + "; " + e.getStackTrace()[0].getMethodName());
        }
    }

    public static void create(String[] args, ApplicationMode mode) {
        Application.mode = mode;

        System.setProperty("app-name", APP_NAME);
        System.setProperty("app-version", VERSION);
        System.setProperty("app-brand", "DaexsysVanilla");
        System.setProperty("no-tunnel-message", "There is no grappl currently open! Start one with 'grappl connect'");
        System.setProperty("no-user-message", "Not logged in. Login with 'login [username] [password]' to use this command.");
        System.setProperty("localadded", "false");
        System.setProperty("serverhost", "localhost");
//
//        JOptionPane.showMessageDialog(null, "Grappl is for sale! The whole service, $8k. Contact ellenhbrt@gmail.com if you are interested.\nClick OK to continue launching Grappl.");
//        functionGroup.getEnvironment().putVar("app-name", "Grappl");
//        functionGroup.getEnvironment().putVar("version", Application.APP_NAME + " " + Application.VERSION + " {Brand=" + Application.BRAND + "}");
//
//        Application.functionGroup.register(new Function() {
//            @Override
//            public void run(Vars vars, String[] strings) {
//                System.out.println("Changing host to: " + strings[0]);
//                System.setProperty("serverhost", strings[0]);
//            }
//        }, "changelocal");
//
//        functionGroup.unregister("println");
//        functionGroup.unregister("pl");
//        functionGroup.register(new Function() {
//            @Override
//            public void run(Vars environment, String[] strings) {
//                String output = "";
//                for (String s : strings) {
//                    output += s + " ";
//                }
//                output = output.substring(0, output.length() - 1);
//
//                log.log(output);
//            }
//        }, "println", "pl");

        if(log == null)
            log = new GrapplLog();

        log.log("Started: Grappl " + VERSION + " {Brand=" + BRAND + ", Mode=" + mode + "}");
        log.log("JVM version: " + System.getProperty("java.version") + " | OS: " + System.getProperty("os.name") + " | Arch: " + System.getProperty("os.arch"));
        log.log("If you encounter issues, please report them to @grapplstatus, or ellenhbrt@gmail.com.");
        log.log("Preferably with a copy of this log! Thx <3");

        log.log("====================");

        getApplicationState();
        commandHandler = new CommandHandler(applicationState);

        System.setErr(new GrapplErrorStream(log, System.out));

        log.log("Preparing to load plugins-");
//        PluginManager.setupAndLoad();
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


        DefaultGUI defaultGUI = new DefaultGUI(getApplicationState());
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
}
