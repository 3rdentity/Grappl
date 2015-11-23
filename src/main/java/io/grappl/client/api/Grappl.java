package io.grappl.client.api;

import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.stable.Authentication;
import io.grappl.client.impl.stable.NetworkLocation;
import io.grappl.client.impl.stable.RelayServerNotFoundException;
import io.grappl.client.impl.stable.StatMonitor;
import io.grappl.client.api.event.UserConnectListener;
import io.grappl.client.api.event.UserDisconnectListener;
import io.grappl.client.impl.gui.DefaultGUI;

import java.util.Collection;
import java.util.UUID;

/**
 * Abstract interface that represents a Grappl tunnel. Can be TCP or UDP.
 */
public interface Grappl {

    public void addUserConnectListener(UserConnectListener userConnectListener);
    public void addUserDisconnectListener(UserDisconnectListener userDisconnectListener);

    public Collection<ClientConnection> getConnectedClients();

    public void useAuthentication(Authentication authentication);
    public Authentication getAuthentication();

    public boolean connect(String relay) throws RelayServerNotFoundException;
    public void disconnect();

    public NetworkLocation getExternalServer();
    public NetworkLocation getInternalServer();

    public ApplicationState getApplicationState();

    public StatMonitor getStatMonitor();

    public UUID getUUID();
    public DefaultGUI getGUI();
}
