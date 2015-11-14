package io.grappl.client.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.stable.Authentication;

import java.util.ArrayList;
import java.util.List;

public class State {

    private Authentication authentication;
    private List<Grappl> grapplList = new ArrayList<Grappl>();

    public void useAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public void addGrappl(Grappl grappl) {
        grapplList.add(grappl);
    }

    public Grappl getPrimaryGrappl() {
        return grapplList.get(0);
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
}
