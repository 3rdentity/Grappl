package com.daexsys.grappl.client;

import com.daexsys.grappl.GrapplGlobal;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URL;

public class GrapplGUI {
    public JFrame jFrame;

    public GrapplGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        jFrame = new JFrame("Grappl Client " + GrapplClientState.VERSION);
        jFrame.setSize(new Dimension(300, 240));

        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.setLayout(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        try {
            jFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(new URL("http://grappl.io:888/html/glogo.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel usernameLable = new JLabel("Username");
        usernameLable.setBounds(5, 5, 250, 20);
        jFrame.add(usernameLable);

        final JTextField username = new JTextField("");
        username.setBounds(5, 25, 250, 20);
        username.setText(GrapplDataFile.getUsername());
        jFrame.add(username);

        final JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(5, 45, 250, 20);
        jFrame.add(passwordLabel);

        final JPasswordField jPasswordField = new JPasswordField("");
        jPasswordField.setBounds(5, 65, 250, 20);
        jFrame.add(jPasswordField);

//            final JCheckBox rememberMe = new JCheckBox();

        JButton login = new JButton("Login");
        login.setBounds(2, 100, 140, 40);
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DataInputStream dataInputStream = null;
                DataOutputStream dataOutputStream = null;

                try {
                    Socket socket = new Socket(GrapplGlobal.DOMAIN, GrapplGlobal.AUTHENTICATION);
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    dataOutputStream.writeByte(0);

                    PrintStream printStream = new PrintStream(dataOutputStream);
                    printStream.println(username.getText().toLowerCase());
                    printStream.println(jPasswordField.getPassword());

                    boolean success = dataInputStream.readBoolean();
                    boolean alpha = dataInputStream.readBoolean();
                    int port = dataInputStream.readInt();
                    Client.isAlphaTester = alpha;
                    Client.isLoggedIn = success;

                    if(success) {
                        ClientLog.log("Logged in as " + username.getText());
                        ClientLog.log("Alpha tester: " + alpha);
                        ClientLog.log("Static port: " + port);
                        Client.username = username.getText();
                        Client.password = jPasswordField.getPassword();

                        GrapplDataFile.saveUsername(username.getText());

                        // options: nyc. sf. pac. lon. deu.
                        String prefix = dataInputStream.readLine();

                        String domain = prefix + "." + GrapplGlobal.DOMAIN;

                        int wX = jFrame.getX();
                        int wY = jFrame.getY();

                        jFrame.setVisible(false);

                        JFrame newJframe = new JFrame(GrapplGlobal.APP_NAME + " Client ("+ Client.username + ")");
                        // 300, 240
                        newJframe.setSize(new Dimension(300, 240));
                        newJframe.setLocation(wX, wY);

                        try {
                            newJframe.setIconImage(Toolkit.getDefaultToolkit().getImage(new URL("http://grappl" +
                                    ".io:888/html/glogo.png")));
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }

                        newJframe.setVisible(true);
                        newJframe.setLayout(null);
                        newJframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                        JButton jButton = new JButton("Close " + GrapplGlobal.APP_NAME + " Client");
                        jButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                System.exit(0);
                            }
                        });
                        newJframe.add(jButton);
                        jButton.setBounds(0, 95, 280, 100);

                        JButton consoleButton = new JButton("C I");
                        consoleButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                new ConsoleWindow();
                            }
                        });
                        consoleButton.setBounds(235, 40, 40, 40);
                        newJframe.add(consoleButton);

                        String ports = JOptionPane.showInputDialog("What port does your server run on?");

                        try {
                            Thread.sleep(330);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }

                        jFrame = newJframe;

                        try {
                            Client.initToRelay(domain, Integer.parseInt(ports));
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(getjFrame(), "The value you entered is not a number");
                        }
                    } else {
                        ClientLog.log("Login failed!");
                    }
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            }
        });
        jFrame.add(login);

        JButton signup = new JButton("Sign up");
        signup.setBounds(142, 100, 140, 40);
        signup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(URI.create("http://grappl.io/register"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        jFrame.add(signup);

        JButton beanonymous = new JButton("Run without logging in");
        //202
        beanonymous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.setVisible(false);
                int wX = jFrame.getX();
                int wY = jFrame.getY();

                jFrame = new JFrame(GrapplGlobal.APP_NAME + " Client");
                jFrame.setSize(new Dimension(300, 240));
                jFrame.setLocation(wX, wY);

                jFrame.setVisible(true);
                jFrame.setLayout(null);
                jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                try {
                    jFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(new URL("http://grappl.io:888/html/glogo.png")));
                } catch (Exception ee) {
                    ee.printStackTrace();
                }

                JButton consoleButton = new JButton("C I");
                consoleButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new ConsoleWindow();
                    }
                });
                consoleButton.setBounds(235, 40, 40, 40);
                jFrame.add(consoleButton);

                JButton jButton = new JButton("Close " + GrapplGlobal.APP_NAME + " Client");
                jButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });
                jFrame.add(jButton);
                jButton.setBounds(0, 95, 280, 100);

                String ports = JOptionPane.showInputDialog("What port does your server run on?");
                Client.initToRelay(GrapplGlobal.DOMAIN, Integer.parseInt(ports));
            }
        });
        beanonymous.setBounds(2, 150, 192, 40);
        jFrame.add(beanonymous);

        JButton donate = new JButton("Donate");
        donate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(URI.create("http://grappl.io/donate"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        donate.setBounds(201, 150, 75, 40);
        jFrame.add(donate);

        jFrame.repaint();
    }

    public void initializeGUI(String relayServerIP, String publicPort, int localPort) {
//        final JLabel jLabel = new JLabel("Public address: " + relayServerIP + ":" + publicPort);
//        jLabel.setBounds(5, 5, 450, 20);
//        getjFrame().add(jLabel);

        final JTextPane label = new JTextPane();
        label.setContentType("text");
        label.setText("Public address: " + relayServerIP + ":" + publicPort);
        label.setBorder(null);
        label.setBackground(null);
        label.setEditable(false);
        label.setBounds(5, 8, 450, 20);
        getjFrame().add(label);

        JLabel jLabel2 = new JLabel("Server on local port: " + localPort);
        jLabel2.setBounds(5, 25, 450, 20);
        getjFrame().add(jLabel2);

        final JLabel jLabel4 = new JLabel("Waiting for data");
        jLabel4.setBounds(5, 65, 450, 20);
        getjFrame().add(jLabel4);

        getjFrame().repaint();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (jLabel4 != null && Client.jLabel3 != null) {
                        Client.jLabel3.setText("Connected clients: " + Client.connectedClients);
                        jLabel4.setText("Sent Data: " + (Client.sent * 4) + "KB - Recv Data: " + (Client.recv * 4) + "KB");
                        getjFrame().repaint();
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public JFrame getjFrame() {
        return jFrame;
    }
}
