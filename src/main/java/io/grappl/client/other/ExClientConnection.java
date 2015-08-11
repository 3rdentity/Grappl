package io.grappl.client.other;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.NetworkLocation;

import java.io.IOException;
import java.net.Socket;

public class ExClientConnection {

    public static final int BLOCK_SIZE = 4096;

    // The address of the external client
    private String address;

    // The Grappl instance associated with this connection
    private Grappl grappl;

    // Whether or not the connection is still open
    private boolean open;

    public ExClientConnection(Grappl grappl, String address) {
        this.grappl = grappl;
        this.address = address;
    }

    /**
     * Open the connection
     * Creates two threads to move data from the internal server to the relay server
     */
    public void open() {
        // Increment the connected player counter.
        getGrappl().getStatsManager().openConnection();

        final int relayPort = Integer.parseInt(grappl.getExternalPort()) + 1;

        // This socket connects to the local server.
        try {
            NetworkLocation internalServer = grappl.getInternalServer();

            final Socket inward = new Socket(internalServer.getAddress(), internalServer.getPort());
            grappl.getSockets().add(inward);
            final Socket outward = new Socket(grappl.getRelayServer(), relayPort);
            grappl.getSockets().add(outward);

            // Start the local -> remote thread
            final Thread outwardCurrent = new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[BLOCK_SIZE];
                    int size;

                    try {
                        while ((size = inward.getInputStream().read(buffer)) != -1) {
                            outward.getOutputStream().write(buffer, 0, size);
                            grappl.getStatsManager().sendBlock();

                            if(grappl.getFreezer() != null) {
                                grappl.getFreezer().sendBlock(buffer);
                            }
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
                            grappl.getStatsManager().receiveBlock();

                            if(grappl.getFreezer() != null) {
                                grappl.getFreezer().receiveBlock(buffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
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
            ClientLog.log("Failed to connect to internal server!");
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
}
