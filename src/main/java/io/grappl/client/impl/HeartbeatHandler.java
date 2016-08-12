package io.grappl.client.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * Creates and tracks connections to relay heartbeat servers.
 *
 * In ye olde days, it was only expected that one instance of the application
 * would only generate one Grappl connection, so it made sense to have the heartbeat
 * connections in the TCPGrappl class. However, now that the application is capable
 * of creating multiple tunnels, it's a total waste to have every single TCPGrappl
 * object handling it's own heartbeats, since you technically only need one per relay.
 *
 * This is where this class comes it. When a Grappl is opened, it checks to see if a heartbeat
 * is already created to the relay it is attempting to connect to. If it already is, nothing
 * more is done. Otherwise, a new one is opened, and recorded.
 */
public class HeartbeatHandler {

    private Set<String> existingHeartbeatThreads = new HashSet<String>();

    public void tryToMakeHeartbeatTo(String relayServer) {
        if(!existingHeartbeatThreads.contains(relayServer)) {
            createHeartBeatThread(relayServer);
            existingHeartbeatThreads.add(relayServer);
        }
    }

    private void createHeartBeatThread(final String relayServer) {
        Thread heartBeatThread = new Thread(new Runnable() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void run() {
                Socket heartBeat;
                DataOutputStream dataOutputStream = null;

                try {
                    heartBeat = new Socket(relayServer, Application.HEARTBEAT_PORT);
                    dataOutputStream = new DataOutputStream(heartBeat.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Application.getLog().log("Connected to heartbeat server @ " + relayServer);

                while (true) {
                    try {
                        dataOutputStream.writeInt(0);
                    } catch (IOException e) {
                        return;
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        heartBeatThread.setName("Grappl Heartbeat Thread " + relayServer);
        heartBeatThread.start();
    }
}
