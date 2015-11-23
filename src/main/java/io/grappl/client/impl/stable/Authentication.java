package io.grappl.client.impl.stable;

import io.grappl.client.impl.Application;
import io.grappl.client.impl.error.AuthenticationFailureException;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;

/**
 * This class represents a authentication/communication session with
 * the core Grappl server.
 *
 * It stores data related to login, and auth state.
 */
public class Authentication {

    private String username;

    private boolean isPremium;
    private boolean loginSuccessful = false;
    private String localizedRelayPrefix;
    private int staticPort;

    // TODO: Please find a way around this. Please. But at least it's optional...
    private JFrame optionalFrame;
    private Socket authSocket;
    private DataInputStream authDataInputStream;
    private DataOutputStream authDataOutputStream;

    private UUID connectionUUID = UUID.randomUUID();

    public UUID getConnectionUUID() {
        return connectionUUID;
    }

    public Authentication() {
        Application.getLog().log("Auth connection created " + getConnectionUUID());
    }

    public Authentication(JFrame jFrame) {
        Application.getLog().log("AAuth connection created " + getConnectionUUID());
        this.optionalFrame = jFrame;
    }

    public void dumpStackTrace() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        for(StackTraceElement stackTraceElement : stackTraceElements) {
            System.out.println(stackTraceElement);
        }
    }

    public void login(String username, char[] password) throws AuthenticationFailureException {
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
                // TODO: tfw depreciated functionality. Find away around this, maybe?
                localizedRelayPrefix = authDataInputStream.readLine();

                isPremium = isPremiumUser;
                loginSuccessful = loginSuccess;

                staticPort = port;
            } catch (SocketTimeoutException e) {
                if(optionalFrame != null)
                    JOptionPane.showMessageDialog(optionalFrame, "Login failed, incorrect username or password");

                throw new AuthenticationFailureException("Wrong credentials for: " + username + " (or account does not exist)");
            }
        } catch (IOException e) {
            if(optionalFrame != null)
                JOptionPane.showMessageDialog(optionalFrame, "Broken connection, authentication failed");

            throw new AuthenticationFailureException("Login failed");
        }
    }

    public void logout() {
        loginSuccessful = false;
        isPremium = false;
        staticPort = -1;
        localizedRelayPrefix = "";

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
