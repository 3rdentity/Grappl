package io.grappl.client.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.api.Protocol;
import io.grappl.client.api.event.GrapplOpenListener;
import io.grappl.client.impl.relay.RelayManager;
import io.grappl.client.impl.relay.RelayServer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApplicationState {

    /*
        Load official relay server list from grappl.io, then ping them all to determine latency.
     */
    private RelayManager relayManager = new RelayManager();
    {
        try {
//            final RelayTransmission relayTransmission =
//                    RelayTransmission.getFromWebLocation(Application.RELAY_LIST_LOC);

            Thread pingThread = new Thread(new Runnable() {
                @Override
                public void run() {
//                    for (RelayServer relayServer : relayTransmission.getRelayServerList()) {
//                        relayManager.offerRelay(relayServer);
//                    }
                relayManager.offerRelay(new RelayServer("n.grappl.io", "East Coast NA"));
                relayManager.offerRelay(new RelayServer("s.grappl.io", "West Coast NA"));
                relayManager.offerRelay(new RelayServer("e.grappl.io", "NL Europe"));
                relayManager.offerRelay(new RelayServer("p.grappl.io", "Aus / Oceania"));
                }
            });
            pingThread.start();
        } catch (Exception e) {
            Application.getLog().log("Error fetching relay list");
        }
    }

    private Authentication authentication;

    private List<Grappl> grapplList = new ArrayList<Grappl>();

    private int focusedIndex = 0;
    private String commandBufferVar;

    private Set<GrapplOpenListener> grapplOpenListeners = new HashSet<GrapplOpenListener>();

    public ApplicationState(){}

    public void useAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public void addGrappl(Grappl grappl) {
        grapplList.add(grappl);

        for(GrapplOpenListener grapplOpenListener : grapplOpenListeners) {
            grapplOpenListener.grapplOpen(grappl);
        }
    }

    public Grappl getFocusedGrappl() {
        if(grapplList.size() > 0) {
            return grapplList.get(focusedIndex);
        }

        return null;
    }

    public int getFocusedIndex() {
        return focusedIndex;
    }

    public void setFocusedIndex(int index) {
        this.focusedIndex = index;
    }

    public void removeGrappl(Grappl grappl) {
        grapplList.remove(grappl);
    }

    public Grappl getGrappl(int index) {
        return grapplList.get(0);
    }

    public int getGrapplCount() {
        return grapplList.size();
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public GrapplBuilder createGrapplBuilder(Protocol protocol) {
        return new GrapplBuilder(this, protocol);
    }

    public void addGrapplOpenListener(GrapplOpenListener grapplOpenListener) {
        grapplOpenListeners.add(grapplOpenListener);
    }

    public RelayManager getRelayManager() {
        return relayManager;
    }

    public void setCommandBufferVar(String commandBufferVar) {
        this.commandBufferVar = commandBufferVar;
    }

    public String getCommandBufferVar() {
        return commandBufferVar;
    }
}
