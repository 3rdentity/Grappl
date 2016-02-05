package io.grappl.client.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.api.LocationProvider;
import io.grappl.client.api.Protocol;
import io.grappl.client.impl.error.AuthenticationException;
import io.grappl.gui.AdvancedGUI;
import io.grappl.gui.DefaultGUI;
import io.grappl.client.impl.tcp.TCPGrappl;

import javax.swing.*;

public class GrapplBuilder {

    private Grappl grappl;
    private NetworkLocation indevNetLoc = new NetworkLocation("", -1);
    private ApplicationState applicationState;

    public GrapplBuilder() {
        grappl = new TCPGrappl(null);
    }

    public GrapplBuilder(ApplicationState applicationState, Protocol grapplProtocol) {
        this.applicationState = applicationState;

        if(grapplProtocol == Protocol.TCP) {
            grappl = new TCPGrappl(applicationState);
        } else {
//            grappl = new UDPGrappl();
        }
    }

    public GrapplBuilder withGUI(DefaultGUI gui) {
        ((TCPGrappl) grappl).setGUI(gui);
        return this;
    }

    public GrapplBuilder withAdvancedGUI(AdvancedGUI gui) {
        ((TCPGrappl) grappl).advancedGUI = gui;
        return this;
    }

    public void atNetworkLocation(NetworkLocation networkLocation) {
        indevNetLoc = networkLocation;
    }

    public GrapplBuilder atLocalAddress(String serverIP) {
        indevNetLoc.setAddress(serverIP);
        return this;
    }

    public GrapplBuilder atLocalPort(int localPort) {
        indevNetLoc.setPort(localPort);
        return this;
    }

    public GrapplBuilder withInternalLocationProvider(LocationProvider locationProvider) {
        ((TCPGrappl) grappl).setInternalServerProvider(locationProvider);
        return this;
    }

    /**
     * JFrame is optional
     */
    public GrapplBuilder login(String username, char[] password, JFrame jFrame) throws AuthenticationException {
        Authentication authentication = new Authentication(jFrame);
        authentication.login(username, password);
        grappl.useAuthentication(authentication);

        return this;
    }

    public Grappl build() {
        ((TCPGrappl) grappl).setInternalServerProvider(new LocationProvider() {
            @Override
            public NetworkLocation getLocation() {
                return indevNetLoc;
            }
        });

        if(applicationState != null) {
            applicationState.addGrappl(grappl);
        } else {
            Application.getLog().log("No state");
        }

        return grappl;
    }
}
