package io.grappl.client.impl.api;

import io.grappl.client.api.Grappl;
import io.grappl.client.api.Protocol;
import io.grappl.client.impl.gui.AdvancedGUI;
import io.grappl.client.impl.gui.StandardGUI;

import javax.swing.*;

public class GrapplBuilder {

    private Grappl grappl;

    public GrapplBuilder() { grappl = new TCPGrappl(); }

    public GrapplBuilder(Protocol grapplProtocol) {
        if(grapplProtocol == Protocol.TCP) {
            grappl = new TCPGrappl();
        }
    }

    public GrapplBuilder withGUI(StandardGUI gui) {
        ((TCPGrappl) grappl).gui = gui;
        return this;
    }

    public GrapplBuilder withAdvancedGUI(AdvancedGUI gui) {
        ((TCPGrappl) grappl).aGUI = gui;
        return this;
    }

    public GrapplBuilder atLocalAddress(String serverIP) {
        ((TCPGrappl) grappl).internalAddress = serverIP;
        return this;
    }

    public GrapplBuilder atLocalPort(int localPort) {
        ((TCPGrappl) grappl).internalPort = localPort;
        return this;
    }

    public GrapplBuilder withInternalLocationProvider(LocationProvider locationProvider) {
        ((TCPGrappl) grappl).locationProvider = locationProvider;
        return this;
    }

    /**
     * JFrame is optional
     */
    public GrapplBuilder login(String username, char[] password, JFrame jFrame) {
        Authentication authentication = new Authentication(jFrame);
        authentication.login(username, password);
        grappl.useAuthentication(authentication);

        return this;
    }

    public Grappl build() {
        return grappl;
    }
}
