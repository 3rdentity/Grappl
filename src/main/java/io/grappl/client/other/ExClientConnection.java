package io.grappl.client.other;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.NetworkLocation;
import io.grappl.client.api.handler.DataHandler;
import io.grappl.client.api.handler.NullHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class ExClientConnection {

    public static final int BLOCK_SIZE = 4096;

    // The address of the external client
    private String address;

    // The Grappl instance associated with this connection
    private Grappl grappl;

    // Whether or not the connection is still open
    private boolean open;

    // Stats related to this specific connection
    private ExConnectionStats exConnectionStats;

    // The UUID of this connection
    private UUID uuid;

    private DataHandler dataHandler;

    public ExClientConnection(Grappl grappl, String address) {
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

        final int relayPort = Integer.parseInt(grappl.getExternalPort()) + 1;

        // This socket connects to the local server.
        try {
            NetworkLocation internalServer = grappl.getInternalServer();

            final Socket inward = new Socket(internalServer.getAddress(), internalServer.getPort());
            grappl.getSockets().add(inward);
            final Socket outward = new Socket(grappl.getRelayServer(), relayPort);
            grappl.getSockets().add(outward);

            ClientLog.detailed(uuid + " connection active " + address.substring(1, address.length())
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
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                    try {
                        inward.close();
                        outward.close();
                    } catch (IOException e) {
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
                        } catch (IOException e1) {                                        }
                    }

                    try {
                        inward.close();
                        outward.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            inwardCurrent.start();
        } catch (Exception e) {
            ClientLog.log("ERROR: Failed to connect " + address.substring(1, address.length()) + " to internal server!");
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
        open = false;
        ClientLog.log(address + " has been disconnected");
    }

    public UUID getUUID() {
        return uuid;
    }

    public ExConnectionStats getExConnectionStats() {
        return exConnectionStats;
    }
}
