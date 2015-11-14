package io.grappl.client.api;

import io.grappl.client.impl.stable.ApplicationState;
import io.grappl.client.impl.stable.Authentication;
import io.grappl.client.impl.stable.NetworkLocation;
import io.grappl.client.impl.stable.StatMonitor;
import io.grappl.client.api.event.UserConnectListener;
import io.grappl.client.api.event.UserDisconnectListener;
import io.grappl.client.impl.gui.StandardGUI;

import java.util.Collection;

/**
 * Abstract interface that represents a Grappl tunnel. Can be TCP or UDP.
 */
public interface Grappl {

    public void addUserConnectListener(UserConnectListener userConnectListener);
    public void addUserDisconnectListener(UserDisconnectListener userDisconnectListener);

    public Collection<ClientConnection> getConnectedClients();

    public void useAuthentication(Authentication authentication);
    public Authentication getAuthentication();

    public boolean connect(String relay);
    public void disconnect();

    public NetworkLocation getExternalServer();
    public NetworkLocation getInternalServer();

    public ApplicationState getApplicationState();

    public String getInternalAddress();
    public int getInternalPort();
    public void setInternalAddress(String address);
    public void setInternalPort(int port);

    public StatMonitor getStatMonitor();

    public StandardGUI getGUI();
}
