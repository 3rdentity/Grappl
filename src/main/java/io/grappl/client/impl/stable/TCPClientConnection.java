package io.grappl.client.impl.stable;

import io.grappl.client.api.ClientConnection;
import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.test.handler.DataHandler;
import io.grappl.client.impl.test.handler.NullHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

/**
 * Represents a connection from a client through a relay server to this machine,
 * in TCP.
 */
public class TCPClientConnection implements ClientConnection {

    public static final int BLOCK_SIZE = 4096;

    // The Grappl instance associated with this connection
    private TCPGrappl tcpGrappl;

    // The address of the external client
    private String address;

    // Whether or not the connection is still open
    private boolean open;

    // Stats related to this specific connection
    private ExConnectionStats exConnectionStats;

    // The UUID of this connection
    private UUID uuid;

    private DataHandler dataHandler;

    private Socket inward;
    private Socket outward;

    public TCPClientConnection(final TCPGrappl grappl, final String address) {
        this.tcpGrappl = grappl;
        this.address = address;

        dataHandler = new NullHandler();
        exConnectionStats = new ExConnectionStats();
        uuid = UUID.randomUUID();
    }

    /**
     * Open the connection
     * Creates two threads to move data from the internal server to the relay server
     */
    public void open() {
        // Increment the connected player counter.
        tcpGrappl.getStatMonitor().openConnection();

        final int relayPort = tcpGrappl.getExternalServer().getPort() + 1;

        // This socket connects to the local server.
        try {
            NetworkLocation internalServer = tcpGrappl.getInternalServer();

            inward = new Socket(internalServer.getAddress(), internalServer.getPort());
            inward.setSoTimeout(10000);
            tcpGrappl.getSockets().add(inward);

            outward = new Socket(tcpGrappl.getExternalServer().getAddress(), relayPort);
            outward.setSoTimeout(10000);
            tcpGrappl.getSockets().add(outward);

            Application.getLog().detailed(uuid + " connection active " + address.substring(1, address.length())
                    + " -> " + internalServer.getAddress() + ":" + internalServer.getPort());

            // Start the local -> remote thread
            final Thread outwardCurrent = new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[BLOCK_SIZE];
                    int size;

                    try {
                        while ((size = inward.getInputStream().read(buffer)) != -1) {
                            outward.getOutputStream().write(buffer, 0, size);
                            tcpGrappl.getStatMonitor().dataSent(size);
                            dataHandler.handleOutgoing(buffer, size);
                        }
                    } catch (IOException e) {
                        try {
                            inward.close();
                            outward.close();
                            acknowledgeDisconnect();
                        } catch (IOException e1) {
                            acknowledgeDisconnect();
                            e1.printStackTrace();
                        }
                    }

                    try {
                        inward.close();
                        outward.close();
                        acknowledgeDisconnect();
                    } catch (IOException e) {
                        acknowledgeDisconnect();
                        e.printStackTrace();
                    }
                }
            });
            outwardCurrent.start();

            // Start the remote -> local thread
            final Thread inwardCurrent = new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[BLOCK_SIZE];
                    int size;

                    try {
                        while ((size = outward.getInputStream().read(buffer)) != -1) {
                            inward.getOutputStream().write(buffer, 0, size);
                            tcpGrappl.getStatMonitor().dataReceived(size);
                            dataHandler.handleIncoming(buffer, size);
                        }
                    } catch (IOException e) {
                        try {
                            inward.close();
                            outward.close();
                            acknowledgeDisconnect();
                        } catch (IOException e1) {
                            acknowledgeDisconnect();                                   }
                    }

                    try {
                        inward.close();
                        outward.close();
                        acknowledgeDisconnect();
                    } catch (IOException e) {
                        acknowledgeDisconnect();
                        e.printStackTrace();
                    }
                }
            });
            inwardCurrent.start();
        } catch (Exception e) {
            Application.getLog().log("ERROR: Failed to connect " + address.substring(1, address.length()) + " to internal server!");
            acknowledgeDisconnect();
        }
    }

    public void close() {
        try {
            inward.close();
            outward.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isOpen() {
        return open;
    }

    public boolean ping() {
        return true;
    }

    public String getAddress() {
        return address;
    }

    public Grappl getGrappl() {
        return tcpGrappl;
    }

    public void acknowledgeDisconnect() {
        if(!open) {
            open = false;
            tcpGrappl.getStatMonitor().closeConnection();
            Application.getLog().log(address + " has been disconnected");
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public ExConnectionStats getExConnectionStats() {
        return exConnectionStats;
    }
}
