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

    public GrapplBuilder useLoginDetails(String username, char[] password) {
        grappl.username = username;
        grappl.password = password;

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
    public GrapplBuilder login(JFrame jFrame) {
        new Authentication(jFrame).login(grappl.username, grappl.password);
        return this;
    }

    public Grappl build() {
        return grappl;
    }
}
