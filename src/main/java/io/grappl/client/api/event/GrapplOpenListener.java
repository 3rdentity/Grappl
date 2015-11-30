package io.grappl.client.api.event;

import io.grappl.client.api.Grappl;

public interface GrapplOpenListener extends Listener {

    public void grapplOpen(Grappl grappl);
}
