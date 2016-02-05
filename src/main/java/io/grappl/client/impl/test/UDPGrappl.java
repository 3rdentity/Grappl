package io.grappl.client.impl.test;

import io.grappl.client.api.ClientConnection;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.Protocol;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.Authentication;
import io.grappl.client.impl.NetworkLocation;
import io.grappl.client.impl.StatMonitor;
import io.grappl.client.api.event.UserConnectListener;
import io.grappl.client.api.event.UserDisconnectListener;
import io.grappl.gui.DefaultGUI;

import java.io.IOException;
import java.net.*;
import java.util.Collection;
import java.util.UUID;

/**
 * Experimental UDP grappl implementation.
 */
public class UDPGrappl implements Grappl {

    private ApplicationState applicationState;
    private UUID uuid;

    private NetworkLocation internalServer;
    private NetworkLocation externalServer;

    /**
     * Constructs a new Grappl
     */
    public UDPGrappl(ApplicationState applicationState) {
        this.applicationState = applicationState;
        this.uuid = UUID.randomUUID();

        Application.getLog()
                .log("Creating grappl connection " + getUUID());
    }

    public boolean connect(final String relayServer) {

        // TODO: Connected to message server 25564
        try {
            DatagramSocket datagramSocket = new DatagramSocket();

            byte[] data = new byte[] { 3, 3, 3, 3 };

            try {
                InetAddress address = InetAddress.getByName("localhost");

                DatagramPacket datagramPacket = new DatagramPacket(data, data.length, address, 333);
                datagramSocket.send(datagramPacket);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

//        try {
////            DatagramSocket datagramSocket = new DatagramSocket(externalServer.getAddress(), 25564);
//
//            // TODO: for each connection, open one connect inwards and one outwards, then route the streams through to each other
//            // TODO: UDP tunnel
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }

        return true;
    }

    @Override
    public void disconnect() {

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

    @Override
    public NetworkLocation getExternalServer() {
        return internalServer;
    }

    @Override
    public NetworkLocation getInternalServer() {
        return externalServer;
    }

    @Override
    public ApplicationState getApplicationState() {
        return applicationState;
    }

    @Override
    public StatMonitor getStatMonitor() {
        return null;
    }

    @Override
    public UUID getUUID() {
        return uuid;
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
        return Protocol.UDP;
    }
}
