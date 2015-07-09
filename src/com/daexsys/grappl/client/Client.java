package com.daexsys.grappl.client;

import com.daexsys.grappl.GrapplGlobal;
import com.daexsys.grappl.client.commands.CommandHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    // Shouldn't be here
    public static JLabel jLabel3;

    // Stats
    public static int sent = 0;
    public static int recv = 0;
    public static int connectedClients = 0;

    // Login related
    public static String username = "Anonymous";
    public static char[] password;
    public static boolean isAlphaTester = false;
    public static boolean isLoggedIn = false;

    // Connection information
    public static String relayServerIP = "";
    public static int localPort = 0;
    public static String publicPort;

    // Gui
    public static GrapplGUI grapplGUI;

    // Socket related
    public static Socket authSocket;
    public static java.util.List<Socket> sockets = new ArrayList<Socket>();

    public static void main(String[] args) {
        boolean displayGui = true;

        // Handle command line arguments
        if(args.length > 1) {
            if (args[0].equalsIgnoreCase("nogui")) {
                displayGui = false;
            }

            localPort = Integer.parseInt(args[1]);
        }

        // If there should be a gui, create it
        if(displayGui) {
            grapplGUI = new GrapplGUI();
            grapplGUI.getjFrame().repaint();
        }

        // Open connection to auth server (@ grappl.io)
        try {
            authSocket = new Socket(GrapplGlobal.DOMAIN, GrapplGlobal.AUTHENTICATION);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start command line command handling thread
        CommandHandler.createCommandThread();
    }

    /**
     * Connects to a relay server.
     * @param relayServerIP the IP address of the relay server to be connected to
     * @param localPort the port that the local server is hosted on
     */
    public static void initToRelay(final String relayServerIP, final int localPort) {
        ClientLog.log("Running: relayserver:" + relayServerIP + " localport:" + localPort);

        Client.relayServerIP = relayServerIP;
        Client.localPort = localPort;

        try {
            // Create socket listener
            final Socket messageSocket = new Socket(relayServerIP, GrapplGlobal.MESSAGE_PORT);
            sockets.add(messageSocket);

            // Get localPort that the server will be hosted on remotely
            final DataInputStream messageInputStream = new DataInputStream(messageSocket.getInputStream());
            publicPort = messageInputStream.readLine();
            // If the message is not gotten back, something went terribly wrong. Need to display an error message.

            ClientLog.log("Hosting on: " + relayServerIP + ":" + publicPort);

            if (grapplGUI != null) {
                grapplGUI.initializeGUI(relayServerIP, publicPort, localPort);
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
    public static void createHeartbeatThread() {
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
    public static void isDown() {
        ClientLog.log("Lost connection to remote");
        closeAllSockets();

        if(isAlphaTester) {
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
                            e.printStackTrace();
                        }

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } else {
            JOptionPane.showMessageDialog(grapplGUI.getjFrame(), "The relay server you were connected to has gone down. Sorry for the interruption!");
        }
    }

    public static void createExClientHandler(final Socket messageSocket, final DataInputStream messageInputStream) {
        Thread exClientHandler = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(grapplGUI != null) {
                        jLabel3 = new JLabel("Connected clients: " + connectedClients);
                        jLabel3.setBounds(5, 45, 450, 20);
                        grapplGUI.getjFrame().add(jLabel3);
                        grapplGUI.getjFrame().repaint();
                    }

                    while(true) {
                        // This goes off when a new client attempts to connect.
                        String userIP = messageInputStream.readLine();
                        ClientLog.log("A user has connected from ip " + userIP);

                        // Increment the connected player counter.
                        connectedClients++;

                        // This socket connects to the local server.
                        try {
                            final Socket toLocal = new Socket("127.0.0.1", localPort);
                            sockets.add(toLocal);
                            // This socket connects to the grappl server, to transfer data from the computer to it.
                            ClientLog.log(relayServerIP);
                            final Socket toRemote = new Socket(relayServerIP, Integer.parseInt(publicPort) + 1);
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
                                            sent += 1;
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
                                            recv += 1;
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
                            ClientLog.log("FAILED TO CONNECT TO LOCAL SERVER. ODD.");
                        }
                    }
                } catch (IOException e) {
                    try {
                        messageSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        });
        exClientHandler.start();
    }

    public static void closeAllSockets() {
        for (int i = 0; i < sockets.size(); i++) {
            try {
                sockets.get(i).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ClientLog.log("Sockets closed");
    }

    public static void restart() {
        ClientLog.log("Reconnecting");

        if(isLoggedIn) {
            DataInputStream dataInputStream;
            DataOutputStream dataOutputStream;

            try {
                Socket socket = new Socket(GrapplGlobal.DOMAIN, GrapplGlobal.AUTHENTICATION);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                dataOutputStream.writeByte(0);

                PrintStream printStream = new PrintStream(dataOutputStream);

                printStream.println(Client.username.toLowerCase());
                printStream.println(Client.password);

                boolean success = dataInputStream.readBoolean();
                boolean alpha = dataInputStream.readBoolean();
                int port = dataInputStream.readInt();
                isAlphaTester = alpha;
                isLoggedIn = success;

                if (success) {
                    ClientLog.log("Logged in as " + Client.username);
                    ClientLog.log("Alpha tester: " + alpha);
                    ClientLog.log("Static port: " + port);

                    // options: nyc. sf. pac. lon. deu.
                    String prefix = dataInputStream.readLine();

                    String domain = prefix + "." + GrapplGlobal.DOMAIN;

                    ClientLog.log(domain);

                    int wX = grapplGUI.getjFrame().getX();
                    int wY = grapplGUI.getjFrame().getY();

                    grapplGUI.getjFrame().setVisible(false);
                    grapplGUI.jFrame = new JFrame(GrapplGlobal.APP_NAME + " Client (" + Client.username + ")");
                    // 300, 240
                    grapplGUI.getjFrame().setSize(new Dimension(300, 240));
                    grapplGUI.getjFrame().setLocation(wX, wY);

                    grapplGUI.getjFrame().setVisible(true);
                    grapplGUI.getjFrame().setLayout(null);
                    grapplGUI.getjFrame().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                    JButton jButton = new JButton("Close " + GrapplGlobal.APP_NAME + " Client");
                    jButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            System.exit(0);
                        }
                    });
                    grapplGUI.getjFrame().add(jButton);
                    jButton.setBounds(0, 95, 280, 100);
                } else {
                    ClientLog.log("Login failed!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        initToRelay(Client.relayServerIP, Client.localPort);
    }
}
