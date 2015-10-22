package io.grappl.client.api;

import io.grappl.client.gui.StandardGUI;

import javax.swing.*;

public class GrapplBuilder {

    private Grappl grappl;

    public GrapplBuilder() {
        grappl = new Grappl();
    }

    public GrapplBuilder withGUI(StandardGUI gui) {
        grappl.gui = gui;
        return this;
    }

    public GrapplBuilder atLocalAddress(String serverIP) {
        grappl.internalAddress = serverIP;
        return this;
    }

    public GrapplBuilder atLocalPort(int localPort) {
        grappl.internalPort = localPort;
        return this;
    }

    public GrapplBuilder withInternalLocationProvider(LocationProvider locationProvider) {
        grappl.locationProvider = locationProvider;
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
