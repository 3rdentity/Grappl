package io.grappl.client.gui;

import io.grappl.GrapplGlobal;
import io.grappl.client.ClientLog;
import io.grappl.client.GrapplClientState;
import io.grappl.client.GrapplDataFile;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.GrapplBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URL;

public class GrapplGUI {
    public JFrame jFrame;
    private Grappl grappl;
    private boolean isActuallyHash = false;

    protected ConsoleWindow theConsoleWindow;

    private static final String commandButton = "...";
    public JLabel jLabel3;

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
        jFrame.setSize(new Dimension(320, 240));

        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.setLayout(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        jFrame.setResizable(false);

        try {
            jFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(new URL("http://grappl.io:888/html/glogo.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        final JLabel usernameLable = new JLabel("Username");
        usernameLable.setBounds(5, 2, 250, 20);
        jFrame.add(usernameLable);

        final JTextField usernamef = new JTextField("");
        usernamef.setBounds(5, 22, 250, 20);
        usernamef.setText(GrapplDataFile.getUsername());
        jFrame.add(usernamef);

        final JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(5, 42, 250, 20);
        jFrame.add(passwordLabel);

        final JPasswordField jPasswordField = new JPasswordField("");
        jPasswordField.setBounds(5, 62, 250, 20);
        String password = GrapplDataFile.getPassword();

        if(password != null) {
            jPasswordField.setText(password);
            isActuallyHash = true;
        } else {
            ClientLog.log("Password is null");
        }
        jFrame.add(jPasswordField);

        final JCheckBox rememberMeBox = new JCheckBox();
        rememberMeBox.setBounds(10, 87, 20, 20);
        jFrame.add(rememberMeBox);

        final JLabel rememberMeLabel = new JLabel("Remember me");
        rememberMeLabel.setBounds(35, 87, 250, 20);
        jFrame.add(rememberMeLabel);

        final GrapplGUI theGUI = this;

        if(isActuallyHash) {
            rememberMeBox.setSelected(true);
            login(usernamef, jPasswordField, this, rememberMeBox);
        } else {

            final JButton login = new JButton("Login");
            login.setBounds(2, 112, 140, 40);
            login.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    login(usernamef, jPasswordField, theGUI, rememberMeBox);
                }
            });
            jFrame.add(login);

            //100
            JButton signup = new JButton("Sign up");
            signup.setBounds(142, 112, 140, 40);
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

                    JButton consoleButton = new JButton(commandButton);
                    consoleButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if(theConsoleWindow == null) {
                                theConsoleWindow = new ConsoleWindow(grappl);
                            } else {
                                theConsoleWindow.getTheFrame().toFront();
                            }
                        }
                    });
                    consoleButton.setBounds(235, 40, 40, 40);
                    jFrame.add(consoleButton);
//                    JButton jButton2 = new JButton("Request random visitors");

                    grappl = new GrapplBuilder().withGUI(theGUI).build();

//                    jButton2.addActionListener(new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//                            try {
//                                Socket socket = new Socket(GrapplGlobal.DOMAIN, GrapplGlobal.AUTHENTICATION);
//                                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
//                                String app = JOptionPane.showInputDialog("What game/application is your server for?");
//                                if (!app.equalsIgnoreCase("")) {
//                                    String send = "";
//                                    ClientLog.log("Adding to server list");
//                                    try {
//                                        dos.writeByte(6);
//                                        PrintStream printStream = new PrintStream(dos);
//                                        send = app + " - " + grappl.getRelayServer() + ":" + grappl.getExternalPort();
//                                        printStream.println(send);
//                                    } catch (Exception ee) {
//                                        ee.printStackTrace();
//                                    }
//                                }
//                            } catch (Exception ex) {
//
//                            }
//                        }
//                    });
//                    jButton2.setBounds(0, 95, 280, 20);
//                    jFrame.add(jButton2);

                    JButton jButton = new JButton("Close " + GrapplGlobal.APP_NAME + " Client");
                    jButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            System.exit(0);
                        }
                    });
                    jFrame.add(jButton);
                    jButton.setBounds(0, 115, 280, 80);

                    String ports = JOptionPane.showInputDialog("What port does your server run on?");
                    grappl.setInternalPort(Integer.parseInt(ports));
                    grappl.connect(GrapplGlobal.DOMAIN);
                }
            });
            //150
            beanonymous.setBounds(2, 155, 192, 40);
            jFrame.add(beanonymous);

            JButton donate = new JButton("Advanced");
            donate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                    try {
//                        Desktop.getDesktop().browse(URI.create("http://grappl.io/donate"));
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
                    jFrame.setVisible(false);
                    new NewGUI().create();
                }
            });
            donate.setBounds(201, 155, 100, 40);
            jFrame.add(donate);

            jFrame.repaint();
        }
    }

    public void initializeGUI(String relayServerIP, String publicPort, int localPort) {
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
                    if (jLabel4 != null && jLabel3 != null) {
                        jLabel3.setText("Connected clients: " + grappl.getStatsManager().getOpenConnections());
                        jLabel4.setText("Sent Data: " + (grappl.getStatsManager().getSentData() * 4) + "KB - Recv Data: " + (0 *  grappl.getStatsManager().getReceivedData()) + "KB");
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

    public void login(JTextField usernamef, JPasswordField jPasswordField, GrapplGUI theGUI, JCheckBox rememberMeBox) {
        DataOutputStream dataOutputStream = null;

        GrapplBuilder grapplBuilder = new GrapplBuilder();

        String username = usernamef.getText().toLowerCase();
        char[] password = jPasswordField.getPassword();

        try {
            if(!isActuallyHash) {
                password = (new String(password).hashCode() + "").toCharArray();
            }

            grapplBuilder.useLoginDetails(username, password).login().withGUI(theGUI);
            grappl = grapplBuilder.build();

            if(grappl.isLoggedIn()) {
                ClientLog.log("Logged in as " + grappl.getUsername());
                ClientLog.log("Alpha tester: " + grappl.isAlphaTester());
                ClientLog.log("Static port: " + grappl.getExternalPort());

                if(!rememberMeBox.isSelected()) {
                    password = null;
                }

                GrapplDataFile.saveUsername(grappl.getUsername(), password);

                // options: nyc. sf. pac. lon. deu.
                String prefix = grappl.getPrefix();

                String domain = prefix + "." + GrapplGlobal.DOMAIN;

                int wX = jFrame.getX();
                int wY = jFrame.getY();

                jFrame.setVisible(false);

                JFrame newJframe = new JFrame(GrapplGlobal.APP_NAME + " Client ("+ grappl.getUsername() + ")");
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
                // 95 100
                jButton.setBounds(0, 115, 280, 80);

//                newJframe.setResizable(false);

                final DataOutputStream dos = dataOutputStream;
//                JButton jButton2 = new JButton("Request random visitors");
//                jButton2.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        String app = JOptionPane.showInputDialog("What game/application is your server for?");
//                        if(!app.equalsIgnoreCase("")) {
//                            String send = "";
//                            ClientLog.log("Adding to server list");
//                            try {
//                                dos.writeByte(6);
//                                PrintStream printStream = new PrintStream(dos);
//                                send = app + " - " + grappl.getRelayServer() + ":" + grappl.getExternalPort();
//                                printStream.println(send);
//                            } catch (Exception ee) {
//                                ee.printStackTrace();
//                            }
//                        }
//
//                    }
//                });
//                newJframe.add(jButton2);
//                // 95 100
//                jButton2.setBounds(0, 95, 280, 20);

                JButton consoleButton = new JButton(commandButton);
                consoleButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(theConsoleWindow == null) {
                            theConsoleWindow = new ConsoleWindow(grappl);
                        } else {
                            theConsoleWindow.getTheFrame().toFront();
                        }
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

                grappl.setInternalPort(Integer.parseInt(ports));
                try {
                    grappl.connect(domain);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(getjFrame(), "The value you entered is not a number");
                }
            } else {
                ClientLog.log("Login failed!");
            }
        } catch (Exception esdfe) {
            esdfe.printStackTrace();
        }
    }
}
