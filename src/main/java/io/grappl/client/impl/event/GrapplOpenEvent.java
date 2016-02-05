package io.grappl.client.impl.event;

import io.grappl.client.api.Grappl;

public class GrapplOpenEvent {

    private Grappl grappl;

    public GrapplOpenEvent(Grappl theGrappl) {
        this.grappl = theGrappl;
    }

    public Grappl getGrappl() {
        return grappl;
    }
}
