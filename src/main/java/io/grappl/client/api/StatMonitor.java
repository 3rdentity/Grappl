package io.grappl.client.api;

import java.io.DataOutputStream;

/**
 * Handles the statistics management for a single Grappl instance.
 *
 * It monitors:
 *      - The bytes sent
 *      - The bytes received
 *      - The connections currently open
 *      - The total connection opened
 *
 * It also will attempt to update the Grappl central server with the sent, received,
 * and total connections statistics every ten seconds.
 */
public class StatMonitor {

    /*
        Variables pertaining to a specific Grappl instance, which are
        tracked throughout the instances' life.
     */
    private int amountSent;
    private int amountReceived;
    private int connectionsOpen;
    private int totalConnections;

    /* The Grappl instance this StatMonitor is associated with */
    private Grappl grappl;

    public StatMonitor(Grappl grappl) {
        this.grappl = grappl;
    }

    public Grappl getGrappl() {
        return grappl;
    }

    public void dataSent(int bytesSent) {
        amountSent += bytesSent;
        bytesInTemp += bytesSent;
        tryUpdatingRemote();
    }

    public void dataReceived(int bytesReceived) {
        amountReceived += bytesReceived;
        bytesOutTemp += bytesReceived;
        tryUpdatingRemote();
    }

    public void openConnection() {
        connectionsOpen++;
        totalConnections++;
        connectionBufferForWebsite++;
    }

    public void closeConnection() {
        connectionsOpen--;
    }

    public int getTotalConnections() {
        return totalConnections;
    }

    /**
     * Website update stats buffer.
     * Displays the bytes sent and received, as well as how many connections have been opened, on your account page.
     */
    private int bytesInTemp;
    private int bytesOutTemp;
    private int connectionBufferForWebsite;
    private long lastTimeSent = System.currentTimeMillis(); // Used to delay the updating, so it doesn't update the website CONSTANTLY.
    
    private Class<? extends Exception> lastException;

    public void tryUpdatingRemote() {
        final int STAT_UPDATE_PACKET_NUMBER = 100;
        final int TEN_SECONDS = 10000;

        if(System.currentTimeMillis() > lastTimeSent + TEN_SECONDS) {
            DataOutputStream dataOutputStream = getGrappl().getAuthentication().getAuthDataOutputStream();

            try {
                dataOutputStream.writeByte(STAT_UPDATE_PACKET_NUMBER);
                dataOutputStream.writeInt(connectionBufferForWebsite);
                dataOutputStream.writeInt(bytesInTemp);
                dataOutputStream.writeInt(bytesOutTemp);

                connectionBufferForWebsite = 0;
                bytesInTemp = 0;
                bytesOutTemp = 0;
            } catch (Exception e) {
            	// This e.printStackTrace() tends to spam the console
                if (!e.getClass().equals(lastException)) {
                    e.printStackTrace();
                    lastException = e.getClass();
                }
            }
        }
    }

    public int getSentDataKB() {
        return amountSent / 1000;
    }

    public int getReceivedKB() {
        return amountReceived / 1000;
    }

    /**
     * In bytes
     */
    public int getSentData() {
        return amountSent;
    }

    /**
     * In bytes
     */
    public int getReceivedData() {
        return amountReceived;
    }

    public int getOpenConnections() {
        return connectionsOpen;
    }

    public void reset() {
        amountSent = 0;
        amountReceived = 0;
        connectionsOpen = 0;
    }
}