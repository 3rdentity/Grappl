package io.grappl.client.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class HeartbeatHandler {

    private static Set<String> existingHeartbeatThreads = new HashSet<String>();

    public static void tryToMakeHeartbeatTo(String relayServer) {
        if(!existingHeartbeatThreads.contains(relayServer)) {
            createHeartBeatThread(relayServer);
            existingHeartbeatThreads.add(relayServer);
        }
    }

    private static void createHeartBeatThread(final String relayServer) {
        Thread heartBeatThread = new Thread(new Runnable() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void run() {
                Socket heartBeat;
                DataOutputStream dataOutputStream = null;

                try {
                    heartBeat = new Socket(relayServer, Application.HEARTBEAT);
                    dataOutputStream = new DataOutputStream(heartBeat.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Application.getLog().log("Connected to heartbeat server");

                while (true) {
                    try {
                        dataOutputStream.writeInt(0);
                    } catch (IOException e) {
                        isDown(relayServer);
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
        heartBeatThread.setName("Grappl Heartbeat Thread");
        heartBeatThread.start();
    }

    // TODO: Make this do something! Argh
    public static void isDown(String relayServer) {}
}
