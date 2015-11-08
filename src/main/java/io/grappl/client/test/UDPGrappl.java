package io.grappl.client.test;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Experimental UDP grappl implementation
 */
public class UDPGrappl {

    protected int localIP;
    protected String relayServer;

    public UDPGrappl() {

    }

    public void connect(final String relayServer) {

        // TODO: Connected to message server 25564
        try {
            DatagramSocket datagramSocket = new DatagramSocket(localIP);

            // TODO: for each connection, open one connect inwards and one outwards, then route the streams through to each other
            // TODO: UDP tunnel
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
