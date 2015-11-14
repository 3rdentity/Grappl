package io.grappl.client.impl.test;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.api.Authentication;
import io.grappl.client.impl.api.NetworkLocation;
import io.grappl.client.impl.api.StatMonitor;
import io.grappl.client.impl.api.event.UserConnectListener;
import io.grappl.client.impl.api.event.UserDisconnectListener;
import io.grappl.client.impl.gui.StandardGUI;

import java.net.DatagramSocket;
import java.net.Socket;
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
    public String getRelayServer() {
        return null;
    }

    @Override
    public int getExternalPort() {
        return 0;
    }

    @Override
    public NetworkLocation getInternalServer() {
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
    public StandardGUI getGUI() {
        return null;
    }
}
