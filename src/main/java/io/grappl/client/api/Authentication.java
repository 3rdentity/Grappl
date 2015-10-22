package io.grappl.client.api;

import io.grappl.GrapplGlobals;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Authentication {

    private String username;

    private boolean isPremium;
    private boolean loginSuccessful = false;
    private String localizedRelayPrefix;
    private int staticPort;

    private JFrame optionalJframe;
    private Socket authSocket;

    public Authentication() {}
    public Authentication(JFrame jFrame) { this.optionalJframe = jFrame; }

    public void login(String username, char[] password) {
        this.username = username;

        try {
            final int timeOut = 2000;

            authSocket = new Socket();
            authSocket.connect(new InetSocketAddress(GrapplGlobals.DOMAIN, GrapplGlobals.AUTHENTICATION), timeOut);
            authSocket.setSoTimeout(timeOut);

            DataInputStream dataInputStream = new DataInputStream(authSocket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(authSocket.getOutputStream());

            try {
                dataOutputStream.writeByte(0);
                PrintStream printStream = new PrintStream(dataOutputStream);
                printStream.println(username);
                printStream.println(password);
            } catch (SocketTimeoutException e) {
                if(optionalJframe != null)
                    JOptionPane.showMessageDialog(optionalJframe, "Broken connection, authentication failed");
            }

            boolean loginSuccess = dataInputStream.readBoolean();
            boolean isPremiumUser = dataInputStream.readBoolean();
            int port = dataInputStream.readInt();

            try {
                localizedRelayPrefix = dataInputStream.readLine();

                isPremium = isPremiumUser;
                loginSuccessful = loginSuccess;

                staticPort = port;
            } catch (SocketTimeoutException e) {
                if(optionalJframe != null)
                    JOptionPane.showMessageDialog(optionalJframe, "Login failed, incorrect username or password");
            }
        } catch (IOException e) {
            if(optionalJframe != null)
                JOptionPane.showMessageDialog(optionalJframe, "Broken connection, authentication failed");
        }
    }

    public void logout() {
        loginSuccessful = false;
        isPremium = false;
        staticPort = -1;
        localizedRelayPrefix = "";
        username = null;

        try {
            authSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        authSocket = null;
    }

    public String getUsername() {
        return username;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public boolean isLoggedIn() {
        return loginSuccessful;
    }

    public String getLocalizedRelayPrefix() {
        return localizedRelayPrefix;
    }

    public int getStaticPort() {
        return staticPort;
    }
}
