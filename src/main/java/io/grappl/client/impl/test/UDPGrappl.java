package io.grappl.client.impl.test;

import io.grappl.client.api.ClientConnection;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.Protocol;
import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.stable.Authentication;
import io.grappl.client.impl.stable.NetworkLocation;
import io.grappl.client.impl.stable.StatMonitor;
import io.grappl.client.api.event.UserConnectListener;
import io.grappl.client.api.event.UserDisconnectListener;
import io.grappl.client.impl.gui.DefaultGUI;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Collection;
import java.util.UUID;

/**
 * Experimental UDP grappl implementation
 */
public class UDPGrappl implements Grappl {

    protected int localIP;
    protected String relayServer;

    public UDPGrappl() {

    }

    @Override
    public void addUserConnectListener(UserConnectListener userConnectListener) {

    }

    @Override
    public void addUserDisconnectListener(UserDisconnectListener userDisconnectListener) {

    }

    @Override
    public Collection<ClientConnection> getConnectedClients() {
        return null;
    }

    @Override
    public void useAuthentication(Authentication authentication) {

    }

    @Override
    public Authentication getAuthentication() {
        return null;
    }

    public boolean connect(final String relayServer) {

        // TODO: Connected to message server 25564
        try {
            DatagramSocket datagramSocket = new DatagramSocket(localIP);

            // TODO: for each connection, open one connect inwards and one outwards, then route the streams through to each other
            // TODO: UDP tunnel
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public NetworkLocation getExternalServer() {
        return null;
    }

    @Override
    public NetworkLocation getInternalServer() {
        return null;
    }

    @Override
    public ApplicationState getApplicationState() {
        return null;
    }

    @Override
    public StatMonitor getStatMonitor() {
        return null;
    }

    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public String getPublicAddress() {
        return null;
    }

    public DefaultGUI getGUI() {
        return null;
    }

    @Override
    public Protocol getProtocol() {
        return null;
    }
}
