package io.grappl.client.impl.stable;

import io.grappl.client.api.ClientConnection;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.LocationProvider;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.log.GrapplLog;
import io.grappl.client.impl.stable.event.UserConnectEvent;
import io.grappl.client.api.event.UserConnectListener;
import io.grappl.client.impl.stable.event.UserDisconnectEvent;
import io.grappl.client.api.event.UserDisconnectListener;
import io.grappl.client.impl.gui.AdvancedGUI;
import io.grappl.client.impl.gui.StandardGUI;

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
public class TCPGrappl implements Grappl {

    private ApplicationState applicationState;

    private GrapplLog clientLog = new GrapplLog();

    // Internal server data
    protected String internalAddress = "127.0.0.1";
    protected int internalPort;

    // External server data
    private NetworkLocation externalServer = new NetworkLocation("", -1);
    protected LocationProvider internalServerProvider; // LocationProvider object that is used to get internal server location

    /* Whether or not the connection with broken using disconnect(). Prevents the connection
     * from automatically re-opening, as it would if the connection was broken unintentionally. */
    private boolean wasIntentionallyDisconnected = false;

    // The GUI associated with this Grappl. Will be null if the advanced GUI is being used
    protected StandardGUI gui;
    public AdvancedGUI advancedGUI;

    // An ExClientConnection is created and stored here for every client that connects through the tunnel.
    // The objects are removed (usually) when the client disconnects, but stray objects have been known to remain.
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Map<UUID, TCPClientConnection> clients = new HashMap<UUID, TCPClientConnection>();

    // Event listeners that listen for user connection/disconnection
    private List<UserConnectListener> userConnectListeners = new ArrayList<UserConnectListener>();
    private List<UserDisconnectListener> userDisconnectListeners = new ArrayList<UserDisconnectListener>();

    private StatMonitor statMonitor = new StatMonitor(this);
    private List<Socket> sockets = new ArrayList<Socket>();

    private UUID uuid = UUID.randomUUID();

    /**
     * Constructs a new Grappl and sets a generic locationprovider
     */
    public TCPGrappl(ApplicationState applicationState) {
        this.applicationState = applicationState;

        Application.getLog().log("Creating grappl connection " + uuid);

        internalServerProvider = new LocationProvider() {
            public NetworkLocation getLocation() {
                return new NetworkLocation(internalAddress, internalPort);
            }
        };
    }

    /**
     * Opens a tunnel to a relay server.
     * @param relayServer the relay server to connect to
     * @return whether or not the connection was succesful
     */
    @SuppressWarnings("SpellCheckingInspection")
    public boolean connect(final String relayServer) {
        externalServer.setAddress(relayServer);

        Application.getLog().log("Connecting: relayserver=" + relayServer + " localport=" + internalPort);

        try {
            // Create socket listener
            final Socket messageSocket = new Socket();

            try {
                final int oneSecondDelay = 1000;

                messageSocket.connect(
                        new InetSocketAddress(relayServer, Application.MESSAGING_PORT),
                        oneSecondDelay);
                sockets.add(messageSocket);

                // Get port that the server will be hosted on remotely
                final DataInputStream messageInputStream = new DataInputStream(messageSocket.getInputStream());
                externalServer.setPort(Integer.parseInt(messageInputStream.readLine()));

                Application.getLog().log("Hosting on: " + relayServer + ":" + externalServer.getPort());

                // If a GUI is associated with this Grappl, do GUI things
                if (gui != null) {
                    gui.initializeGUI(relayServer, externalServer.getPort() + "", internalPort);
                    Application.getLog().log("GUI aspects initialized");
                }

                // Create heartbeat thread that is used to monitor whether or not the client is still connected to the server.
                createHeartbeatThread();
                // Create thread that routes incoming connections to the local server.
                createClientHandler(messageSocket, messageInputStream);

            } catch (SocketTimeoutException e) {
                if(gui != null)
                    JOptionPane.showMessageDialog(gui.getFrame(),
                            "Connection to relay server failed.\nIf this continues, go to Advanced Mode and connect to a different relay server.");

                if(advancedGUI != null) {
                    JOptionPane.showMessageDialog(advancedGUI.getFrame(),
                            "Connection to relay server failed.");

                    advancedGUI.triggerClosing();
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
        if(wasIntentionallyDisconnected) return;

        Application.getLog().log("Reconnecting...");

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
////                    Application.getLog().log("Logged in as " + username);
////                    Application.getLog().log("Alpha tester: " + alpha);
////                    Application.getLog().log("Static port: " + port);
////
////                    // options: nyc. sf. pac. lon. deu.
////                    String prefix = dataInputStream.readLine();
////
////                    String domain = prefix + "." + GrapplGlobals.DOMAIN;
////
////                    Application.getLog().log(domain);
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
////                    Application.getLog().log("Login failed!");
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//        }

        connect(externalServer.getAddress());
    }

    /**
     * Return the address that this server is publicly
     * available at.
     */
    public String getPublicAddress() {
        return getExternalServer().toString();
    }

    public int getInternalPort() {
        return internalPort;
    }

    public void setInternalAddress(String internalAddress) {
        this.internalAddress = internalAddress;
    }

    public void disconnect() {
        wasIntentionallyDisconnected = true;
        closeAllSockets();
    }

    @Override
    public NetworkLocation getExternalServer() {
        return externalServer;
    }

    public void useAuthentication(Authentication authentication) {
        applicationState.useAuthentication(authentication);
    }

    public Authentication getAuthentication() {
        return applicationState.getAuthentication();
    }

    public StandardGUI getGUI() {
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
        return internalServerProvider.getLocation();
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

    @Override
    public Collection<ClientConnection> getConnectedClients() {
        return null;
    }

    public void userDisconnects(UserDisconnectEvent userDisconnectEvent) {
        for (UserDisconnectListener userDisconnectListener : userDisconnectListeners) {
            userDisconnectListener.userDisconnected(userDisconnectEvent);
        }
    }

    private void createClientHandler(final Socket messageSocket, final DataInputStream messageInputStream) {
        final TCPGrappl theGrappl = this;

        final List<TCPClientConnection> connectedClients = new ArrayList<TCPClientConnection>();

        if(gui != null) {
            getGUI().getConnectedClientsLabel().setText("Connected clients: " + getStatMonitor().getOpenConnections());
            getGUI().getConnectedClientsLabel().setBounds(5, 45, 450, 20);
            gui.getFrame().add(getGUI().getConnectedClientsLabel());
            gui.getFrame().repaint();
        }

        Thread clientHandlerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        // This goes off when a new client attempts to connect.
                        String userIP = messageInputStream.readLine();
                        Application.getLog().log("A user has connected from ip " + userIP.substring(1, userIP.length()));

                        userConnects(new UserConnectEvent(userIP));

                        TCPClientConnection exClient = new TCPClientConnection(theGrappl, userIP);
                        exClient.open();
                        clients.put(exClient.getUUID(), exClient);
                        connectedClients.add(exClient);
                    }
                } catch (IOException e) {
                    try {
                        messageSocket.close();
                        Application.getLog().log("Connection with message server has been broken. Unfortunate.");
                    } catch (IOException ignore) {}
                }
            }
        });
        clientHandlerThread.setName("Grappl Client Handler Thread " + getUUID().toString());
        clientHandlerThread.start();

        Thread clientVerifier = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        for (int i = 0; i < connectedClients.size(); i++) {
                            TCPClientConnection exClient = connectedClients.get(i);

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
        clientVerifier.setName("Grappl Client Verifier Thread " + getUUID().toString());
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

        Application.getLog().log("Sockets closed");
    }

    /**
     * This method is called when the connection is lost. Happens
     * when the heartbeat thread is interrupted.
     */
    private void isDown() {
        Application.getLog().log("Lost connection to remote");
        closeAllSockets();

        Thread reconnectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket testSocket = new Socket(Application.DOMAIN, Application.HEARTBEAT);
                        testSocket.close();
                        restart();
                        return;
                    } catch (IOException e) {
                        Application.getLog().log("Attempting reconnect");
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        reconnectThread.setName("Grappl Reconnect Thread");
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
                    heartBeat = new Socket(externalServer.getAddress(), Application.HEARTBEAT);
                    sockets.add(heartBeat);
                    dataOutputStream = new DataOutputStream(heartBeat.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Application.getLog().log("Connected to heartbeat server");

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

    public GrapplLog getLog() {
        return clientLog;
    }

    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String toString() {
        return
                "TCP | " +
                        getInternalServer().toString()
                        + " <-> " +
                        getExternalServer().toString() +
                        " ( " + getUUID() + " )";
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }
}
