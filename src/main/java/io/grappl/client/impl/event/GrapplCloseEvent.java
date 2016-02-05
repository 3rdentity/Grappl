package io.grappl.client.impl.event;

import io.grappl.client.api.Grappl;

public class GrapplCloseEvent {

    private Grappl grappl;

    public GrapplCloseEvent(Grappl theGrappl) {
        this.grappl = theGrappl;
    }

    public Grappl getGrappl() {
        return grappl;
    }
}
