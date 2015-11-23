package io.grappl.client.impl.log;

public class SilentGrapplLog extends GrapplLog {

    @Override
    public void log(String message) {
        // do nothing
    }

    @Override
    public void detailed(String message) {
        // do nothing
    }
}
