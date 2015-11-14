package io.grappl.client.impl.stable.event;

import io.grappl.client.api.ClientConnection;

public class UserConnectEvent {

    private ClientConnection tcpClientConnection;
    private String address;

    public UserConnectEvent(String address, ClientConnection tcpClientConnection) {
        this.address = address;
        this.tcpClientConnection = tcpClientConnection;
    }

    public ClientConnection getClientConnection() {
        return tcpClientConnection;
    }

    public String getAddress() {
        return address;
    }
}
