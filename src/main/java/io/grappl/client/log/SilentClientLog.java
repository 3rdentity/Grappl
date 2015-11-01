package io.grappl.client.log;

public class SilentClientLog extends ClientLog {

    @Override
    public void log(String message) {
        // do nothing
    }

    @Override
    public void detailed(String message) {
        // do nothing
    }
}
