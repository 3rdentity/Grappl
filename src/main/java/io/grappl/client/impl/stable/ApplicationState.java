package io.grappl.client.impl.stable;

import io.grappl.client.api.Grappl;
import io.grappl.client.api.Protocol;
import io.grappl.client.api.event.GrapplOpenListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApplicationState {

    private Authentication authentication;
    private List<Grappl> grapplList = new ArrayList<Grappl>();

    private Set<GrapplOpenListener> grapplOpenListeners = new HashSet<GrapplOpenListener>();

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
            return grapplList.get(0);
        }
        return null;
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
}
