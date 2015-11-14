package io.grappl.client.impl.stable;

import io.grappl.client.impl.Application;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;

public class Authentication {

    private String username;

    private boolean isPremium;
    private boolean loginSuccessful = false;
    private String localizedRelayPrefix;
    private int staticPort;

    private JFrame optionalFrame;
    private Socket authSocket;
    private DataInputStream authDataInputStream;
    private DataOutputStream authDataOutputStream;

    private UUID connectionUUID = UUID.randomUUID();

    public UUID getConnectionUUID() {
        return connectionUUID;
    }

    public Authentication() {
        Application.getLog().log("AUTHENTICATION CONNECTION CREATED " + getConnectionUUID());
    }

    public Authentication(JFrame jFrame) {
        Application.getLog().log("AUTHENTICATION CONNECTION CREATED " + getConnectionUUID());
        this.optionalFrame = jFrame;
    }

    public void dumpStackTrace() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        for(StackTraceElement stackTraceElement : stackTraceElements) {
            System.out.println(stackTraceElement);
        }
    }

    public void login(String username, char[] password) {
        this.username = username;

        try {
            final int timeOut = 2000;

            authSocket = new Socket();
            authSocket.connect(new InetSocketAddress(Application.DOMAIN, Application.AUTHENTICATION), timeOut);
            authSocket.setSoTimeout(timeOut);

            this.authDataInputStream = new DataInputStream(authSocket.getInputStream());
            this.authDataOutputStream = new DataOutputStream(authSocket.getOutputStream());

            try {
                authDataOutputStream.writeByte(0);
                PrintStream printStream = new PrintStream(authDataOutputStream);
                printStream.println(username);
                printStream.println(password);
            } catch (SocketTimeoutException e) {
                if(optionalFrame != null)
                    JOptionPane.showMessageDialog(optionalFrame, "Broken connection, authentication failed");
            }

            boolean loginSuccess = authDataInputStream.readBoolean();
            boolean isPremiumUser = authDataInputStream.readBoolean();
            int port = authDataInputStream.readInt();

            try {
                localizedRelayPrefix = authDataInputStream.readLine();

                isPremium = isPremiumUser;
                loginSuccessful = loginSuccess;

                staticPort = port;
            } catch (SocketTimeoutException e) {
                if(optionalFrame != null)
                    JOptionPane.showMessageDialog(optionalFrame, "Login failed, incorrect username or password");
            }
        } catch (IOException e) {
            if(optionalFrame != null)
                JOptionPane.showMessageDialog(optionalFrame, "Broken connection, authentication failed");
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

    public DataInputStream getAuthDataInputStream() {
        return authDataInputStream;
    }

    public DataOutputStream getAuthDataOutputStream() {
        return authDataOutputStream;
    }

    @Override
    public String toString() {
        return "Authentication{" +
                "username='" + username + '\'' +
                '}';
    }
}
