package io.grappl.client.impl.stable.tcp;

import io.grappl.client.api.ClientConnection;
import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.stable.ConnectionStats;
import io.grappl.client.impl.stable.NetworkLocation;
import io.grappl.client.impl.stable.tcp.TCPGrappl;
import io.grappl.client.impl.test.handler.DataHandler;
import io.grappl.client.impl.test.handler.GenericHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a connection from a client through a relay server to this machine,
 * in TCP.
 */
public class TCPClientConnection implements ClientConnection {

    private final static int ONE_SECOND_DELAY = 1000;
    public static final int BLOCK_SIZE = 4096;

    // The Grappl instance associated with this connection
    private TCPGrappl tcpGrappl;

    // The address of the external client
    private String address;

    // Whether or not the connection is still open
    private boolean open = true;

    // Stats related to this specific connection
    private ConnectionStats connectionStats;

    // The UUID of this connection
    private UUID uuid;

    private DataHandler dataHandler;

    private List<OutputStream> inwardStreams = new ArrayList<OutputStream>();
    private List<OutputStream> outwardStreams = new ArrayList<OutputStream>();

    private Socket inward;
    private Socket outward;

    protected TCPClientConnection(final TCPGrappl grappl, final String address) {
        this.tcpGrappl = grappl;
        this.address = address;

        dataHandler = new GenericHandler();
        connectionStats = new ConnectionStats();
        uuid = UUID.randomUUID();
    }

    /**
     * Open the connection
     * Creates two threads to move data from the internal server to the relay server
     */
    public void open() {
        // Increment the connected player counter.
        tcpGrappl.getStatMonitor().incrementConnectionCount();

        final int relayPort = tcpGrappl.getExternalServer().getPort() + 1;

        try {
            final NetworkLocation internalServer = tcpGrappl.getInternalServer();

            // This socket connects to the internal server.
            inward = new Socket(internalServer.getAddress(), internalServer.getPort());
            inward.setSoTimeout(10 * ONE_SECOND_DELAY);
            inwardStreams.add(inward.getOutputStream());
            tcpGrappl.getSockets().add(inward);

            // This socket connects to the external traffic server.
            outward = new Socket(tcpGrappl.getExternalServer().getAddress(), relayPort);
            outward.setSoTimeout(10 * ONE_SECOND_DELAY);
            outwardStreams.add(outward.getOutputStream());
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
                            size = (int) dataHandler.handleOutgoing(buffer, size, outwardStreams);
                            tcpGrappl.getStatMonitor().dataSent(size);
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
                            size = (int) dataHandler.handleIncoming(buffer, size, inwardStreams);
                            tcpGrappl.getStatMonitor().dataReceived(size);
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

    /**
     * Optional data handler that can be set by a plugin to do cool things with a specific protocol.
     */
    public void setDataHandler(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    public void acknowledgeDisconnect() {
        if(open) {
            open = false;
            tcpGrappl.getStatMonitor().closeConnection();
            Application.getLog().log(address + " has been disconnected from " + tcpGrappl.getUUID());
        }
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public Grappl getGrappl() {
        return tcpGrappl;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void addInwardStream(OutputStream outputStream) {
        inwardStreams.add(outputStream);
    }

    public void addOutwardStream(OutputStream outputStream) {
        outwardStreams.add(outputStream);
    }

    public ConnectionStats getConnectionStats() {
        return connectionStats;
    }

    public Socket getInward() {
        return inward;
    }

    public Socket getOutward() {
        return outward;
    }

    // TODO: Is there a difference between this an ping? Is there a point?
    public boolean isOpen() {
        return open;
    }

    // TODO: Give this some meat or scrap the idea entirely!
    public boolean ping() {
        return true;
    }
}
