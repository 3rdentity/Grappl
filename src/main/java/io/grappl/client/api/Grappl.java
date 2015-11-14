package io.grappl.client.api;

import io.grappl.client.impl.api.Authentication;
import io.grappl.client.impl.api.NetworkLocation;
import io.grappl.client.impl.api.StatMonitor;
import io.grappl.client.impl.api.event.UserConnectListener;
import io.grappl.client.impl.api.event.UserDisconnectListener;
import io.grappl.client.impl.gui.StandardGUI;

import java.net.Socket;
import java.util.Collection;

/**
 * Abstract interface that represents a Grappl tunnel. Can be TCP or UDP.
 */
public interface Grappl {

    public void addUserConnectListener(UserConnectListener userConnectListener);
    public void addUserDisconnectListener(UserDisconnectListener userDisconnectListener);

    public void useAuthentication(Authentication authentication);
    public Authentication getAuthentication();

    public boolean connect(String relay);
    public void disconnect();

    public String getRelayServer();
    public int getExternalPort();

    public NetworkLocation getInternalServer();
    public String getInternalAddress();
    public int getInternalPort();
    public void setInternalAddress(String address);
    public void setInternalPort(int port);

    public StatMonitor getStatMonitor();

    public StandardGUI getGUI();
}
