package io.grappl.client.impl.stable.event;

import io.grappl.client.api.ClientConnection;

public class UserDisconnectEvent {

    private ClientConnection clientConnection;
    private String address;

    public UserDisconnectEvent(String address, ClientConnection clientConnection) {
        this.address = address;
        this.clientConnection = clientConnection;
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public String getAddress() {
        return address;
    }
}
