package io.grappl.client.api.user;

public class DataStats {

    private long bytesOfDataUploaded;
    private long bytesOfDataDownloaded;

    private long serversOpened;
    private long connectedClients;

    public long getBytesOfDataUploaded() {
        return bytesOfDataUploaded;
    }

    public long getBytesOfDataDownloaded() {
        return bytesOfDataDownloaded;
    }

    public long getServersOpened() {
        return serversOpened;
    }

    public long getConnectedClients() {
        return connectedClients;
    }
}
