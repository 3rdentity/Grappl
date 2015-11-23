package io.grappl.client.impl.stable.event;

import io.grappl.client.api.ClientConnection;

public class UserDisconnectEvent {

    private String address;
    private ClientConnection clientConnection;

    public UserDisconnectEvent(String address, ClientConnection clientConnection) {
        this.address = address;
        this.clientConnection = clientConnection;
    }

    public String getAddress() {
        return address;
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }
}
