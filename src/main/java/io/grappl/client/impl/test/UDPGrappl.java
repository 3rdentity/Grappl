package io.grappl.client.impl.test;

import io.grappl.client.api.ClientConnection;
import io.grappl.client.api.Grappl;
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
    public void setInternalServer(NetworkLocation networkLocation) {

    }

    @Override
    public ApplicationState getApplicationState() {
        return null;
    }

    @Override
    public String getInternalAddress() {
        return null;
    }

    @Override
    public int getInternalPort() {
        return 0;
    }

    @Override
    public void setInternalAddress(String address) {

    }

    @Override
    public void setInternalPort(int port) {

    }

    @Override
    public StatMonitor getStatMonitor() {
        return null;
    }

    @Override
    public DefaultGUI getGUI() {
        return null;
    }
}
