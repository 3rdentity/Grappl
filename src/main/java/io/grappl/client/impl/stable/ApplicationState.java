package io.grappl.client.impl.stable;

import io.grappl.client.api.Grappl;
import io.grappl.client.api.Protocol;

import java.util.ArrayList;
import java.util.List;

public class ApplicationState {

    private Authentication authentication;
    private List<Grappl> grapplList = new ArrayList<Grappl>();

    public void useAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public void addGrappl(Grappl grappl) {
        grapplList.add(grappl);
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
}
