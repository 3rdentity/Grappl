package io.grappl.client.impl.stable;

import io.grappl.client.api.Grappl;
import io.grappl.client.api.LocationProvider;
import io.grappl.client.api.Protocol;
import io.grappl.client.impl.gui.AdvancedGUI;
import io.grappl.client.impl.gui.DefaultGUI;
import io.grappl.client.impl.test.UDPGrappl;

import javax.swing.*;

public class GrapplBuilder {

    private Grappl grappl;
    private ApplicationState applicationState;

    public GrapplBuilder() { grappl = new TCPGrappl(null); }

    protected GrapplBuilder(ApplicationState applicationState, Protocol grapplProtocol) {
        this.applicationState = applicationState;

        if(grapplProtocol == Protocol.TCP) {
            grappl = new TCPGrappl(applicationState);
        } else {
            grappl = new UDPGrappl();
        }
    }

    public GrapplBuilder withGUI(DefaultGUI gui) {
        ((TCPGrappl) grappl).gui = gui;
        return this;
    }

    public GrapplBuilder withAdvancedGUI(AdvancedGUI gui) {
        ((TCPGrappl) grappl).advancedGUI = gui;
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
        ((TCPGrappl) grappl).internalServerProvider = locationProvider;
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
        if(applicationState != null) {
            applicationState.addGrappl(grappl);
        }

        return grappl;
    }
}
