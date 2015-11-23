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

    private final static int ONE_SECOND_DELAY = 1000;

    private UUID uuid;

    private ApplicationState applicationState = Application.getApplicationState();

    /* Stores all the sockets associated with this Grappl instance.
       Primarily used to kill a connection entirely by closing them all.
     */
    private List<Socket> sockets = new ArrayList<Socket>();

    protected LocationProvider internalServerProvider;
    private NetworkLocation externalServer = new NetworkLocation("", -1);

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

    // Event listeners that listen for user connection/disconnections.
    private List<UserConnectListener> userConnectListeners = new ArrayList<UserConnectListener>();
    private List<UserDisconnectListener> userDisconnectListeners = new ArrayList<UserDisconnectListener>();

    private StatMonitor statMonitor = new StatMonitor(this);

    /**
     * Constructs a new Grappl and sets a generic LocationProvider.
     */
    public TCPGrappl(ApplicationState applicationState) {
        this.applicationState = applicationState;
        this.uuid = UUID.randomUUID();

        Application.getLog()
                .log("Creating grappl connection " + getUUID());
    }

    /**
     * Opens a tunnel to a relay server.
     * @param relayServer the relay server to connect to
     * @return whether or not the connection was succesful
     */
    @SuppressWarnings("SpellCheckingInspection")
    public boolean connect(final String relayServer) throws RelayServerNotFoundException {
        externalServer.setAddress(relayServer);

        try {
            /* This socket will receive messages from the relay server
            when it is time to open a new tunnel for a client.
             */
            final Socket relayMessageSocket = new Socket();

            try {
                // Connect message socket to the relay server
                relayMessageSocket.connect(
                        new InetSocketAddress(relayServer, Application.MESSAGING_PORT),
                        ONE_SECOND_DELAY);
                sockets.add(relayMessageSocket);

                Application.getLog().log(
                        "Connection opened: relayserver=" + externalServer.getAddress()
                        + " localport=" + getInternalServer().getPort());

                // Receive port that the server will be hosted on externally.
                // TODO: Protocol overhaul. Should just send an int.
                DataInputStream relayMsgInputStream = new DataInputStream(relayMessageSocket.getInputStream());
                externalServer.setPort(Integer.parseInt(relayMsgInputStream.readLine()));

                Application.getLog().log("Hosting on: " + getExternalServer());

                // If a GUI is associated with this Grappl, do GUI things
                // TODO: GrapplOpenEvent, move this to GUI plugin
                if (gui != null)
                    gui.initializeGUI(relayServer, externalServer.getPort() + "", getInternalPort());

                HeartbeatHandler.tryToMakeHeartbeatTo(externalServer.getAddress());

                // Create thread that routes incoming connections to the local server.
                createClientHandler(relayMessageSocket, relayMsgInputStream);

            } catch (SocketTimeoutException e) {
                if(gui != null)
                    JOptionPane.showMessageDialog(gui.getFrame(),
                            "Connection to relay server failed.\n" +
                                    "If this continues, go to Advanced Mode and connect to a different relay server.");

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

    @Override
    public void disconnect() {
        wasIntentionallyDisconnected = true;
        closeAllSockets();
    }

    @Deprecated
    public void restart() {
        // We only want it to restart if the connection was accidentally broken.
        if(wasIntentionallyDisconnected)
            return;

        Application.getLog().log("Reconnecting...");

        try {
            connect(externalServer.getAddress());
        } catch (RelayServerNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Should cause a complete disconnection from Grappl's servers.
     * disconnect() should be used instead, otherwise it may just try to reconnect.
     */
    private void closeAllSockets() {
        for (Socket socket : sockets) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Application.getLog().log("All sockets closed for " + getUUID());
    }

    /**
     * Creates the thread that listens for incoming external clients.
     * @param relayMsgSocket the socket that is used to receive messages from the relay server
     * @param relayMsgInputStream the pre-created input stream associated with the socket
     */
    private void createClientHandler(final Socket relayMsgSocket,
                                     final DataInputStream relayMsgInputStream) {
        // Literally this. Used because code inside Runnables can't access this instance using 'this'.
        final TCPGrappl thisGrappl = this;

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
                        // Receive IP of connected client from relay.
                        String userIP = relayMsgInputStream.readLine();

                        Application.getLog().log("A user has connected from ip "
                                + userIP.substring(1, userIP.length()));

                        TCPClientConnection clientConnection
                                = new TCPClientConnection(thisGrappl, userIP);

                        userConnect(new UserConnectEvent(userIP, clientConnection));

                        clientConnection.open();

                        clients.put(clientConnection.getUUID(), clientConnection);
                        connectedClients.add(clientConnection);
                    }
                } catch (IOException e) {
                    try {
                        relayMsgSocket.close();
                        Application.getLog().log(
                                "Connection with relay server has been broken. Unfortunate.");
                    } catch (IOException ignore) {}
                }
            }
        });
        clientHandlerThread.setName("Grappl Client Handler Thread " + getUUID().toString());
        clientHandlerThread.start();
    }

    @Override
    public void addUserConnectListener(UserConnectListener userConnectListener) {
        userConnectListeners.add(userConnectListener);
    }

    @Override
    public void addUserDisconnectListener(UserDisconnectListener userDisconnectListener) {
        userDisconnectListeners.add(userDisconnectListener);
    }

    public void userConnect(UserConnectEvent userConnectEvent) {
        for(UserConnectListener userConnectListener : userConnectListeners) {
            userConnectListener.userConnected(userConnectEvent);
        }
    }

    public void useDisconnect(UserDisconnectEvent userDisconnectEvent) {
        for (UserDisconnectListener userDisconnectListener : userDisconnectListeners) {
            userDisconnectListener.userDisconnected(userDisconnectEvent);
        }
    }

    @Override
    public NetworkLocation getInternalServer() {
        return internalServerProvider.getLocation();
    }

    @Override
    public NetworkLocation getExternalServer() {
        return externalServer;
    }

    @Override
    public StatMonitor getStatMonitor() {
        return statMonitor;
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
        return Application.getLog();
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

    public List<Socket> getSockets() {
        return sockets;
    }

    public int getInternalPort() {
        return getInternalServer().getPort();
    }

    @Override
    public void useAuthentication(Authentication authentication) {
        Application.getApplicationState().useAuthentication(authentication);
    }

    @Override
    public Collection<ClientConnection> getConnectedClients() {
        return null;
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
