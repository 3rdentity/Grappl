package io.grappl.client.impl.api;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.test.handler.DataHandler;
import io.grappl.client.impl.test.handler.NullHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class ExClientConnection {

    public final int BLOCK_SIZE = 4096;

    // The address of the external client
    private String address;

    // The Grappl instance associated with this connection
    private TCPGrappl grappl;

    // Whether or not the connection is still open
    private boolean open;

    // Stats related to this specific connection
    private ExConnectionStats exConnectionStats;

    // The UUID of this connection
    private UUID uuid;

    private DataHandler dataHandler;

    public ExClientConnection(TCPGrappl grappl, String address) {
        this.grappl = grappl;
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
        getGrappl().getStatMonitor().openConnection();

        final int relayPort = grappl.getExternalPort() + 1;

        // This socket connects to the local server.
        try {
            NetworkLocation internalServer = grappl.getInternalServer();

            final Socket inward = new Socket(internalServer.getAddress(), internalServer.getPort());
            inward.setSoTimeout(10000);
            grappl.getSockets().add(inward);
            final Socket outward = new Socket(grappl.getRelayServer(), relayPort);
            outward.setSoTimeout(10000);
            grappl.getSockets().add(outward);

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
                            grappl.getStatMonitor().dataSent(size);
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
                            grappl.getStatMonitor().dataReceived(size);
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
        return grappl;
    }

    public void acknowledgeDisconnect() {
        if(!open) {
            open = false;
            grappl.getStatMonitor().closeConnection();
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
