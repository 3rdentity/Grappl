package io.grappl.client.api;

import java.io.DataOutputStream;

public class StatMonitor {

    private int amountSent;
    private int amountReceived;
    private int connectionsOpen;
    private int totalConnections;

    private Grappl grappl;

    public StatMonitor(Grappl grappl) {
        this.grappl = grappl;
    }

    public void dataSent(int size) {
        amountSent += size;
        attemptSend();
    }

    public void dataReceived(int size) {
        amountReceived += size;
        attemptSend();
    }

    public void openConnection() {
        connectionsOpen++;
        totalConnections++;
        connectionBufferForWebsite++;
    }

    public void closeConnection() {
        connectionsOpen--;
    }

    public Grappl getGrappl() {
        return grappl;
    }

    public int getTotalConnections() {
        return totalConnections;
    }

    /**
     * Website update stats buffer.
     */
    private int bytesInTemp;
    private int bytesOutTemp;
    private int connectionBufferForWebsite;
    private long lastTimeSent = System.currentTimeMillis(); // Used to delay the updating, so it doesn't update the website CONSTANTLY.

    public void attemptSend() {
        final int TEN_SECONDS = 10000;

        if(System.currentTimeMillis() > lastTimeSent + TEN_SECONDS) {
            DataOutputStream dataOutputStream = getGrappl().getAuthentication().getAuthDataOutputStream();

            try {
                dataOutputStream.writeByte(100);
                dataOutputStream.writeInt(connectionBufferForWebsite);
                dataOutputStream.writeInt(bytesInTemp);
                dataOutputStream.writeInt(bytesOutTemp);

                connectionBufferForWebsite = 0;
                bytesInTemp = 0;
                bytesOutTemp = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getSentDataKB() {
        return amountSent / 1000;
    }

    public int getReceivedKB() {
        return amountReceived / 1000;
    }
    public int getSentData() {
        return amountSent;
    }

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
