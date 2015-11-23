package io.grappl.client.impl.stable;

import io.grappl.client.api.ClientConnection;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.LocationProvider;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.HeartbeatHandler;
import io.grappl.client.impl.log.GrapplLog;
import io.grappl.client.impl.stable.event.UserConnectEvent;
import io.grappl.client.api.event.UserConnectListener;
import io.grappl.client.impl.stable.event.UserDisconnectEvent;
import io.grappl.client.api.event.UserDisconnectListener;
import io.grappl.client.impl.gui.AdvancedGUI;
import io.grappl.client.impl.gui.DefaultGUI;

import javax.swing.*;
import java.io.DataInputStream;
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

    private ApplicationState applicationState = Application.getApplicationState();
    private List<Socket> sockets = new ArrayList<Socket>();

    private GrapplLog clientLog = new GrapplLog();

    // External server data
    private NetworkLocation externalServer = new NetworkLocation("", -1);
    protected LocationProvider internalServerProvider; // LocationProvider object that is used to get internal server location

    /* Whether or not the connection with broken using disconnect(). Prevents the connection
     * from automatically re-opening, as it would if the connection was broken unintentionally. */
    private boolean wasIntentionallyDisconnected = false;

    // The GUI associated with this Grappl. Will be null if the advanced GUI is being used
    protected DefaultGUI gui;
    public AdvancedGUI advancedGUI;

    // An ExClientConnection is created and stored here for every client that connects through the tunnel.
    // The objects are removed (usually) when the client disconnects, but stray objects have been known to remain.
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Map<UUID, TCPClientConnection> clients = new HashMap<UUID, TCPClientConnection>();

    // Event listeners that listen for user connection/disconnection
    private List<UserConnectListener> userConnectListeners = new ArrayList<UserConnectListener>();
    private List<UserDisconnectListener> userDisconnectListeners = new ArrayList<UserDisconnectListener>();

    private StatMonitor statMonitor = new StatMonitor(this);
    private UUID uuid = UUID.randomUUID();

    /**
     * Constructs a new Grappl and sets a generic locationprovider
     */
    public TCPGrappl(ApplicationState applicationState) {
        this.applicationState = applicationState;

        Application.getLog().log("Creating grappl connection " + uuid);
    }

    /**
     * Opens a tunnel to a relay server.
     * @param relayServer the relay server to connect to
     * @return whether or not the connection was succesful
     */
    @SuppressWarnings("SpellCheckingInspection")
    public boolean connect(final String relayServer) throws RelayServerNotFoundException {
        externalServer.setAddress(relayServer);

        Application.getLog().log("Connecting: relayserver=" + relayServer + " localport=" + getInternalPort());

        try {
            // Create socket listener
            final Socket messageSocket = new Socket();

            try {
                final int oneSecondDelay = 1000;

                messageSocket.connect(
                        new InetSocketAddress(relayServer, Application.MESSAGING_PORT),
                        oneSecondDelay);
                sockets.add(messageSocket);

                // Get port that server will be hosted on externally
                DataInputStream messageInputStream = new DataInputStream(messageSocket.getInputStream());
                externalServer.setPort(Integer.parseInt(messageInputStream.readLine()));

                Application.getLog().log("Hosting on: " + relayServer + ":" + externalServer.getPort());

                // TODO: GrapplOpenEvent, move this to GUI plugin
                // If a GUI is associated with this Grappl, do GUI things
                if (gui != null) {
                    gui.initializeGUI(relayServer, externalServer.getPort() + "", getInternalPort());
                }

                HeartbeatHandler.tryToMakeHeartbeatTo(externalServer.getAddress());

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
            throw new RelayServerNotFoundException();
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

        try {
            connect(externalServer.getAddress());
        } catch (RelayServerNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the address that this server is publicly
     * available at.
     */
    public String getPublicAddress() {
        return getExternalServer().toString();
    }

    public int getInternalPort() {
        return getInternalServer().getPort();
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
        Application.getApplicationState().useAuthentication(authentication);
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

                        TCPClientConnection exClient = new TCPClientConnection(theGrappl, userIP);
                        userConnects(new UserConnectEvent(userIP, exClient));

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

    public GrapplLog getLog() {
        return clientLog;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setGUI(DefaultGUI gui) {
        this.gui = gui;
    }

    public void setInternalServerProvider(LocationProvider internalServerProvider) {
        this.internalServerProvider = internalServerProvider;
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }

    public Authentication getAuthentication() {
        return Application.getApplicationState().getAuthentication();
    }

    public DefaultGUI getGUI() {
        return gui;
    }

    public StatMonitor getStatMonitor() {
        return statMonitor;
    }

    public List<Socket> getSockets() {
        return sockets;
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
}
