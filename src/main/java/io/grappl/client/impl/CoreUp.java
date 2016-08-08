package io.grappl.client.impl;

import java.net.Socket;

public class CoreUp {

    public static boolean isCoreUp() {
        try {
            Socket socket = new Socket(Application.DOMAIN, 80);
            return true;
        } catch (Exception up) {
            return false;
        }
    }
}
