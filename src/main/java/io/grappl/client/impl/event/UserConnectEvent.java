package io.grappl.client.impl.event;

import io.grappl.client.api.ClientConnection;

public class UserConnectEvent {

    private ClientConnection clientConnection;
    private String address;

    public UserConnectEvent(String address, ClientConnection clientConnection) {
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
