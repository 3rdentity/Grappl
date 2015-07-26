package com.daexsys.grappl.client.api;

import com.daexsys.grappl.GrapplGlobal;
import com.daexsys.grappl.client.ClientLog;
import com.daexsys.grappl.client.GrapplGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Grappl {

    protected String username;
    protected char[] password;

    protected String externalPort;
    protected int internalPort;

    private String relayServerIP;

    protected GrapplGUI gui;

    protected boolean isLoggedIn = false;
    protected boolean isAlphaTester = false;

    protected String prefix;

    private StatsManager statsManager = new StatsManager();
    private List<Socket> sockets = new ArrayList<Socket>();

    public void connect(String relayServer) {
        ClientLog.log("Connecting: relayserver=" + relayServer + " localport=" +
                internalPort);

        this.relayServerIP = relayServer;

        try {
            // Create socket listener
            final Socket messageSocket = new Socket(relayServer, GrapplGlobal.MESSAGE_PORT);
            sockets.add(messageSocket);

            // Get localPort that the server will be hosted on remotely
            final DataInputStream messageInputStream = new DataInputStream(messageSocket.getInputStream());
            externalPort = messageInputStream.readLine();
            // If the message is not gotten back, something went terribly wrong. Need to display an error message.

            ClientLog.log("Hosting on: " + relayServer + ":" + externalPort);

            if (gui != null) {
                gui.initializeGUI(relayServer, externalPort, internalPort);
                ClientLog.log("GUI aspects initialized");
            }

            // Create heartbeat thread that is used to monitor whether or not the client is still connected to the server.
            createHeartbeatThread();

            // Create thread that routes incoming connections to the local server.
            createExClientHandler(messageSocket, messageInputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Creates a heartbeat thread to the relay server this client is connect to
     */
    private void createHeartbeatThread() {
        Thread heartBeatThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket heartBeat;
                DataOutputStream dataOutputStream = null;

                try {
                    heartBeat = new Socket(relayServerIP, GrapplGlobal.HEARTBEAT);
                    sockets.add(heartBeat);
                    dataOutputStream = new DataOutputStream(heartBeat.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ClientLog.log("Connected to heartbeat server");

                while(true) {
                    try {
                        dataOutputStream.writeInt(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                        isDown();
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

    /**
     * This method is called when the connection is lost. Happens
     * when the heartbeat thread is interrupted.
     */
    private void isDown() {
        ClientLog.log("Lost connection to remote");
        closeAllSockets();

//        if(isAlphaTester) {
            // Attempt reconnect
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Socket testSocket = new Socket(GrapplGlobal.DOMAIN, GrapplGlobal.HEARTBEAT);
                            testSocket.close();
                            restart();
                            return;
                        } catch (IOException e) {
//                            e.printStackTrace();
                            System.out.println("Attempting reconnect");
                        }

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
//        }
//    else {
//            JOptionPane.showMessageDialog(gui.getjFrame(), "The relay server you were connected to has gone " +
//                    "down. Sorry for the interruption!");
//        }
    }

    private void closeAllSockets() {
        for (int i = 0; i < sockets.size(); i++) {
            try {
                sockets.get(i).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ClientLog.log("Sockets closed");
    }

    public int getInternalPort() {
        return internalPort;
    }

    private void createExClientHandler(final Socket messageSocket, final DataInputStream messageInputStream) {
        Thread exClientHandler = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(gui != null) {
                        getGui().jLabel3 = new JLabel("Connected clients: " + getStatsManager().getOpenConnections());
                        getGui().jLabel3.setBounds(5, 45, 450, 20);
                        gui.getjFrame().add(getGui().jLabel3);
                        gui.getjFrame().repaint();
                    }

                    while(true) {
                        // This goes off when a new client attempts to connect.
                        String userIP = messageInputStream.readLine();
                        ClientLog.log("A user has connected from ip " + userIP);

                        // Increment the connected player counter.
                        statsManager.openConnection();

                        // This socket connects to the local server.
                        try {
                            final Socket toLocal = new Socket("127.0.0.1", internalPort);
                            sockets.add(toLocal);
                            // This socket connects to the grappl server, to transfer data from the computer to it.
                            ClientLog.log(relayServerIP);
                            final Socket toRemote = new Socket(relayServerIP, Integer.parseInt(externalPort) + 1);
                            sockets.add(toRemote);

                            // Start the local -> remote thread
                            final Thread localToRemote = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    byte[] buffer = new byte[4096];
                                    int size;

                                    try {
                                        while ((size = toLocal.getInputStream().read(buffer)) != -1) {
                                            toRemote.getOutputStream().write(buffer, 0, size);
                                            statsManager.sendBlock();
                                        }
                                    } catch (IOException e) {
                                        try {
                                            toLocal.close();
                                            toRemote.close();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                    try {
                                        toLocal.close();
                                        toRemote.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            localToRemote.start();

                            //                            Start the remote -> local thread
                            final Thread remoteToLocal = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    byte[] buffer = new byte[4096];
                                    int size;

                                    try {
                                        while ((size = toRemote.getInputStream().read(buffer)) != -1) {
                                            toLocal.getOutputStream().write(buffer, 0, size);
                                            statsManager.receiveBlock();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        try {
                                            toLocal.close();
                                            toRemote.close();
                                        } catch (IOException e1) {                                        }
                                    }

                                    try {
                                        toLocal.close();
                                        toRemote.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            remoteToLocal.start();
                        } catch (Exception e) {
                            ClientLog.log("Failed to connect to local server!");
                        }
                    }
                } catch (IOException e) {
                    try {
                        messageSocket.close();
                        ClientLog.log("Error in connection with message server");
                    } catch (IOException e1) {
//                        e1.printStackTrace();
                    }
//                    e.printStackTrace();
                }
            }
        });
        exClientHandler.start();
    }



    public void restart() {
        ClientLog.log("Reconnecting...");

        if(isLoggedIn) {
            DataInputStream dataInputStream;
            DataOutputStream dataOutputStream;

            try {
                Socket socket = new Socket(GrapplGlobal.DOMAIN, GrapplGlobal.AUTHENTICATION);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                dataOutputStream.writeByte(0);

                PrintStream printStream = new PrintStream(dataOutputStream);

                printStream.println(username.toLowerCase());
                printStream.println(password);

                boolean success = dataInputStream.readBoolean();
                boolean alpha = dataInputStream.readBoolean();
                int port = dataInputStream.readInt();
                isAlphaTester = alpha;
                isLoggedIn = success;

                if (success) {
                    ClientLog.log("Logged in as " + username);
                    ClientLog.log("Alpha tester: " + alpha);
                    ClientLog.log("Static port: " + port);

                    // options: nyc. sf. pac. lon. deu.
                    String prefix = dataInputStream.readLine();

                    String domain = prefix + "." + GrapplGlobal.DOMAIN;

                    ClientLog.log(domain);

                    if(gui != null) {
                        int wX = gui.getjFrame().getX();
                        int wY = gui.getjFrame().getY();

                        gui.getjFrame().setVisible(false);
                        gui.jFrame = new JFrame(GrapplGlobal.APP_NAME + " Client (" + username + ")");
                        // 300, 240
                        gui.getjFrame().setSize(new Dimension(300, 240));
                        gui.getjFrame().setLocation(wX, wY);

                        gui.getjFrame().setVisible(true);
                        gui.getjFrame().setLayout(null);
                        gui.getjFrame().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                        JButton jButton = new JButton("Close " + GrapplGlobal.APP_NAME + " Client");
                        jButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                System.exit(0);
                            }
                        });
                        gui.getjFrame().add(jButton);
                        jButton.setBounds(0, 95, 280, 100);
                    }
                } else {
                    ClientLog.log("Login failed!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        connect(relayServerIP);
    }

    public String getPublicAddress() {
        String publicAddress = "grappl.io:" + getExternalPort();

        if(getPrefix() != null) {
            return getPrefix() + "." + publicAddress;
        }

        return "";
    }

    public void disconnect() {
        closeAllSockets();
    }

    public String getExternalPort() {
        return externalPort;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public void setAlphaTester(boolean isAlphaTester) {
        this.isAlphaTester = isAlphaTester;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAlphaTester() {
        return isAlphaTester;
    }

    public String getRelayServer() {
        return relayServerIP;
    }

    public GrapplGUI getGui() {
        return gui;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setInternalPort(int internalPort) {
        this.internalPort = internalPort;
    }

    public StatsManager getStatsManager() {
        return statsManager;
    }
}
