package io.grappl.client.api;

import io.grappl.GrapplGlobals;
import io.grappl.client.Application;
import io.grappl.client.log.ClientLog;
import io.grappl.client.api.event.UserConnectEvent;
import io.grappl.client.api.event.UserConnectListener;
import io.grappl.client.api.event.UserDisconnectEvent;
import io.grappl.client.api.event.UserDisconnectListener;
import io.grappl.client.gui.AdvancedGUI;
import io.grappl.client.gui.StandardGUI;
import io.grappl.client.other.ExClientConnection;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Represents a Grappl connection.
 *
 * Contains data related to login: {username, password, etc}
 * as well as the state of the connection, such as the port, relay server, etc.
 */
public class Grappl {

    /* Data relating to the user who is logged in. */
    private Authentication authentication;

    // Internal server data
    protected String internalAddress = "127.0.0.1";
    protected int internalPort;

    // External server data
    protected String relayServerIP;
    protected String externalPort;
    protected LocationProvider locationProvider; // LocationProvider object that is used to get internal server location

    /* Whether or not the connection with broken using disconnect(). Prevents the connection
     * from automatically re-opening, as it would if the connection was broken unintentionally. */
    private boolean intentionallyDisconnected = false;

    // The GUI associated with this Grappl. Will be null if the advanced GUI is being used
    protected StandardGUI gui;
    public AdvancedGUI aGUI;

    // An ExClientConnection is created and stored here for every client that connects through the tunnel.
    // The objects are removed (usually) when the client disconnects, but stray objects have been known to remain.
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Map<UUID, ExClientConnection> clients = new HashMap<UUID, ExClientConnection>();

    // Event listeners that listen for user connection/disconnection
    private List<UserConnectListener> userConnectListeners = new ArrayList<UserConnectListener>();
    private List<UserDisconnectListener> userDisconnectListeners = new ArrayList<UserDisconnectListener>();

    // Various experiments and such
    private StatMonitor statMonitor = new StatMonitor(this);
    private List<Socket> sockets = new ArrayList<Socket>();

    private UUID uuid = UUID.randomUUID();

    /**
     * Constructs a new Grappl and sets a generic locationprovider
     */
    public Grappl() {

        Application.getClientLog().log("Creating grappl connection " + uuid);
        // Allows the terminal console to have commands act on the newest grappl object

        // Start command line command handling thread
        Application.getCommandHandler().createConsoleCommandListenThread(this);

        locationProvider = new LocationProvider() {
            public NetworkLocation getLocation() {
                return new NetworkLocation(internalAddress, internalPort);
            }
        };
    }

    /**
     * Opens a tunnel to a relay server.
     * @param relayServer the relay server to connect to
     */
    @SuppressWarnings("SpellCheckingInspection")
    public boolean connect(final String relayServer) {
        this.relayServerIP = relayServer;

        Application.getClientLog().log("Connecting: relayserver=" + relayServer + " localport=" + internalPort);

        try {
            // Create socket listener
            final Socket messageSocket = new Socket();

            try {
                final int oneSecondDelay = 1000;
                messageSocket.connect(new InetSocketAddress(relayServer, GrapplGlobals.MESSAGING_PORT), oneSecondDelay);
                sockets.add(messageSocket);

                // Get port that the server will be hosted on remotely
                final DataInputStream messageInputStream = new DataInputStream(messageSocket.getInputStream());
                externalPort = messageInputStream.readLine();

                Application.getClientLog().log("Hosting on: " + relayServer + ":" + externalPort);

                // If a GUI is associated with this Grappl, do GUI things
                if (gui != null) {
                    gui.initializeGUI(relayServer, externalPort, internalPort);
                    Application.getClientLog().log("GUI aspects initialized");
                }

                // Create heartbeat thread that is used to monitor whether or not the client is still connected to the server.
                createHeartbeatThread();

                // Create thread that routes incoming connections to the local server.
                createExClientHandler(messageSocket, messageInputStream);

            } catch (SocketTimeoutException e) {
                if(gui != null)
                    JOptionPane.showMessageDialog(gui.getFrame(),
                            "Connection to relay server failed.\nIf this continues, go to Advanced Mode and connect to a different relay server.");

                if(aGUI != null) {
                    JOptionPane.showMessageDialog(aGUI.getFrame(),
                            "Connection to relay server failed.");

                    aGUI.triggerClosing();
                }

                return false;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Deprecated
    public void restart() {
        if(intentionallyDisconnected) return;

        Application.getClientLog().log("Reconnecting...");

//        if(getAuthentication().isLoggedIn()) {
////            DataInputStream dataInputStream;
////            DataOutputStream dataOutputStream;
////
////            try {
////                Socket socket = new Socket(GrapplGlobals.DOMAIN, GrapplGlobals.AUTHENTICATION);
////                dataInputStream = new DataInputStream(socket.getInputStream());
////                dataOutputStream = new DataOutputStream(socket.getOutputStream());
////
////                dataOutputStream.writeByte(0);
////
////                PrintStream printStream = new PrintStream(dataOutputStream);
////
////                printStream.println(username.toLowerCase());
////                printStream.println(password);
////
////                boolean success = dataInputStream.readBoolean();
////                boolean alpha = dataInputStream.readBoolean();
////                int port = dataInputStream.readInt();
////                isPremium = alpha;
////                isLoggedIn = success;
////
////                if (success) {
////                    Application.getClientLog().log("Logged in as " + username);
////                    Application.getClientLog().log("Alpha tester: " + alpha);
////                    Application.getClientLog().log("Static port: " + port);
////
////                    // options: nyc. sf. pac. lon. deu.
////                    String prefix = dataInputStream.readLine();
////
////                    String domain = prefix + "." + GrapplGlobals.DOMAIN;
////
////                    Application.getClientLog().log(domain);
////
////                    if(gui != null) {
////                        int wX = gui.getFrame().getX();
////                        int wY = gui.getFrame().getY();
////
////                        gui.getFrame().setVisible(false);
////                        gui.jFrame = new JFrame(GrapplGlobals.APP_NAME + " Client (" + username + ")");
////                        // 300, 240
////                        gui.getFrame().setSize(new Dimension(300, 240));
////                        gui.getFrame().setLocation(wX, wY);
////
////                        gui.getFrame().setVisible(true);
////                        gui.getFrame().setLayout(null);
////                        gui.getFrame().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
////
////                        JButton jButton = new JButton("Close " + GrapplGlobals.APP_NAME + " Client");
////                        jButton.addActionListener(new ActionListener() {
////                            public void actionPerformed(ActionEvent e) {
////                                System.exit(0);
////                            }
////                        });
////                        gui.getFrame().add(jButton);
////                        jButton.setBounds(0, 95, 280, 100);
////                    }
////                } else {
////                    Application.getClientLog().log("Login failed!");
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//        }

        connect(relayServerIP);
    }

    public String getPublicAddress() {
        return getRelayServer() + ":" + getExternalPort();

//        if(getAuthentication().getLocalizedRelayPrefix() != null) {
//            return getAuthentication().getLocalizedRelayPrefix() + "." + publicAddress;
//        }

//        return "";
    }

    public int getInternalPort() {
        return internalPort;
    }

    public void setInternalAddress(String internalAddress) {
        this.internalAddress = internalAddress;
    }

    public void disconnect() {
        intentionallyDisconnected = true;
        closeAllSockets();
    }

    public String getExternalPort() {
        return externalPort;
    }

    public String getUsername() {
        return getAuthentication().getUsername();
    }

    public void useAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public String getRelayServer() {
        return relayServerIP;
    }

    public StandardGUI getGui() {
        return gui;
    }

    public void setInternalPort(int internalPort) {
        this.internalPort = internalPort;
    }

    public StatMonitor getStatMonitor() {
        return statMonitor;
    }

    public String getInternalAddress() {
        return internalAddress;
    }

    public List<Socket> getSockets() {
        return sockets;
    }

    public NetworkLocation getInternalServer() {
        return locationProvider.getLocation();
    }

    public void addUserConnectListener(UserConnectListener userConnectListener) {
        userConnectListeners.add(userConnectListener);
    }

    public void userConnects(UserConnectEvent userConnectEvent) {
        for(UserConnectListener userConnectListener : userConnectListeners) {
            userConnectListener.userConnected(userConnectEvent);
        }
    }

    public void addUserDisconnectListener(UserDisconnectListener userDisconnectListener) {
        userDisconnectListeners.add(userDisconnectListener);
    }

    public void userDisconnects(UserDisconnectEvent userDisconnectEvent) {
        for (UserDisconnectListener userDisconnectListener : userDisconnectListeners) {
            userDisconnectListener.userDisconnected(userDisconnectEvent);
        }
    }

    private void createExClientHandler(final Socket messageSocket, final DataInputStream messageInputStream) {
        final Grappl theGrappl = this;

        final List<ExClientConnection> connectedClients = new ArrayList<ExClientConnection>();

        if(gui != null) {
            getGui().getConnectedClientsLabel().setText("Connected clients: " + getStatMonitor().getOpenConnections());
            getGui().getConnectedClientsLabel().setBounds(5, 45, 450, 20);
            gui.getFrame().add(getGui().getConnectedClientsLabel());
            gui.getFrame().repaint();
        }

        Thread exClientHandler = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        // This goes off when a new client attempts to connect.
                        String userIP = messageInputStream.readLine();
                        Application.getClientLog().log("A user has connected from ip " + userIP.substring(1, userIP.length()));

                        userConnects(new UserConnectEvent(userIP));

                        ExClientConnection exClient = new ExClientConnection(theGrappl, userIP);
                        exClient.open();
                        clients.put(exClient.getUUID(), exClient);
                        connectedClients.add(exClient);
                    }
                } catch (IOException e) {
                    try {
                        messageSocket.close();
                        Application.getClientLog().log("Connection with message server has been broken. Unfortunate.");
                    } catch (IOException ignore) {}
                }
            }
        });
        exClientHandler.start();

        Thread clientVerifier = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        for (int i = 0; i < connectedClients.size(); i++) {
                            ExClientConnection exClient = connectedClients.get(i);

                            if(!exClient.ping()) {
                                connectedClients.remove(exClient);
                            }
                        }

                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        clientVerifier.start();
    }

    /**
     * Should cause a complete disconnection from Grappl's servers.
     * disconnect() should be used instead, otherwise it may just try to reconnect.
     */
    private void closeAllSockets() {
        for (int i = 0; i < sockets.size(); i++) {
            try {
                sockets.get(i).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Application.getClientLog().log("Sockets closed");
    }

    /**
     * This method is called when the connection is lost. Happens
     * when the heartbeat thread is interrupted.
     */
    private void isDown() {
        Application.getClientLog().log("Lost connection to remote");
        closeAllSockets();

        Thread reconnectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket testSocket = new Socket(GrapplGlobals.DOMAIN, GrapplGlobals.HEARTBEAT);
                        testSocket.close();
                        restart();
                        return;
                    } catch (IOException e) {
                        Application.getClientLog().log("Attempting reconnect");
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        reconnectThread.setName("Reconnect Thread");
        reconnectThread.start();
    }

    /**
     * Creates a heartbeat thread to the relay server this client is connect to
     */
    private void createHeartbeatThread() {
        Thread heartBeatThread = new Thread(new Runnable() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void run() {
                Socket heartBeat;
                DataOutputStream dataOutputStream = null;

                try {
                    heartBeat = new Socket(relayServerIP, GrapplGlobals.HEARTBEAT);
                    sockets.add(heartBeat);
                    dataOutputStream = new DataOutputStream(heartBeat.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Application.getClientLog().log("Connected to heartbeat server");

                while(true) {
                    try {
                        dataOutputStream.writeInt(0);
                    } catch (IOException e) {
                        isDown();
                        return;
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        heartBeatThread.setName("Grappl Heartbeat Thread");
        heartBeatThread.start();
    }

    private ClientLog clientLog = new ClientLog();
    public ClientLog getLog() {
        return clientLog;
    }

    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String toString() {
        return
                "TCP | " +
                        getInternalServer().getAddress() + ":" + getInternalServer().getPort()
                        + " <-> " +
                        getRelayServer() + ":" + getExternalPort() +
                        " ( " + getUUID() + " )";
    }
}
