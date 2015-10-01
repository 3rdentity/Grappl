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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.URI;

/**
 * The standard Grappl gui.
 */
public class GrapplGUI {

    private static final String COMMAND_BUTTON_TEXT = "...";

    public JFrame jFrame;
    private Grappl grappl;
    private boolean isActuallyHash = false;
    protected ConsoleWindow theConsoleWindow;
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

        jFrame = new JFrame("Grappl " + GrapplClientState.VERSION);
        jFrame.setSize(new Dimension(310, 240));
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.setLayout(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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
        jPasswordField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                GrapplClientState.usingSavedHashPass = false;
            }

            @Override
            public void keyPressed(KeyEvent e) {
                GrapplClientState.usingSavedHashPass = false;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                GrapplClientState.usingSavedHashPass = false;
            }
        });
        String password = GrapplDataFile.getPassword();
        jFrame.add(jPasswordField);
        if(password != null) {
            jPasswordField.setText(password);
            isActuallyHash = true;
        } else {
            ClientLog.log("Password is null");
        }

        final JCheckBox rememberMeBox = new JCheckBox();
        rememberMeBox.setBounds(10, 87, 20, 20);
        jFrame.add(rememberMeBox);
        final JLabel rememberMeLabel = new JLabel("Remember me");
        rememberMeLabel.setBounds(35, 87, 250, 20);
        jFrame.add(rememberMeLabel);

        final GrapplGUI theGUI = this;
        if(isActuallyHash) {
            rememberMeBox.setSelected(true);
        } {
            final JButton logInButton = new JButton("Log in");
            logInButton.setBounds(4, 112, 140, 40);
            logInButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    login(usernamef, jPasswordField, theGUI, rememberMeBox);
                }
            });
            jFrame.add(logInButton);

            JButton signUpButton = new JButton("Sign up");
            signUpButton.setBounds(148, 112, 140, 40);
            signUpButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Desktop.getDesktop().browse(URI.create("http://grappl.io/register"));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            jFrame.add(signUpButton);

            JButton beAnonymousButton = new JButton("Run without logging in");
            beAnonymousButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jFrame.setVisible(false);
                    int wX = jFrame.getX();
                    int wY = jFrame.getY();

                    jFrame = new JFrame(GrapplGlobal.APP_NAME + " ");
                    jFrame.setSize(new Dimension(300, 240));
                    jFrame.setLocation(wX, wY);
                    jFrame.setVisible(true);
                    jFrame.setLayout(null);
                    jFrame.setResizable(false);
                    jFrame.setSize(new Dimension(290, 230));
                    jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    jFrame.setIconImage(GrapplClientState.getIcon());

                    JButton consoleButton = new JButton(COMMAND_BUTTON_TEXT);
                    consoleButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (theConsoleWindow == null) {
                                theConsoleWindow = new ConsoleWindow(grappl);
                            } else {
                                theConsoleWindow.getTheFrame().toFront();
                            }
                        }
                    });
                    consoleButton.setBounds(235, 40, 40, 40);
                    jFrame.add(consoleButton);

                    grappl = new GrapplBuilder().useLoginDetails("default", ("1".hashCode() + "").toCharArray()).login().withGUI(theGUI).build();

                    JButton jButton = new JButton("Close " + GrapplGlobal.APP_NAME + " Client");
                    jButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            System.exit(0);
                        }
                    });
                    jFrame.add(jButton);
                    jButton.setBounds(2, 105, 280, 90);

                    String ports = JOptionPane.showInputDialog("What port does your server run on?");
                    grappl.setInternalPort(Integer.parseInt(ports));
                    grappl.connect(grappl.getPrefix() + "." + GrapplGlobal.DOMAIN);
                }
            });
            beAnonymousButton.setBounds(4, 155, 192, 40);
            jFrame.add(beAnonymousButton);

            JButton donateButton = new JButton("Advanced");
            donateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jFrame.setVisible(false);
                    new AdvancedGUI().create();
                }
            });
            donateButton.setBounds(200, 155, 90, 40);
            jFrame.add(donateButton);

            jFrame.setIconImage(GrapplClientState.getIcon());
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

        /* GUI update thread */
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

        GrapplBuilder grapplBuilder = new GrapplBuilder();

        String username = usernamef.getText().toLowerCase();
        char[] password = jPasswordField.getPassword();

        try {
            if(!isActuallyHash || !GrapplClientState.usingSavedHashPass) {
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
                newJframe.setResizable(false);
                newJframe.setSize(new Dimension(290, 230));
                newJframe.setIconImage(GrapplClientState.getIcon());
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

                jButton.setBounds(2, 105, 280, 90);

                JButton consoleButton = new JButton(COMMAND_BUTTON_TEXT);
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
