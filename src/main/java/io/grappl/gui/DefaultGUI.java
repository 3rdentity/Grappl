package io.grappl.gui;

import io.grappl.client.api.Grappl;
import io.grappl.client.api.Protocol;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.GrapplDataFile;
import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.relay.AdaptiveConnector;
import io.grappl.client.impl.error.AuthenticationException;
import io.grappl.client.impl.GrapplBuilder;
import io.grappl.client.impl.error.RelayServerNotFoundException;

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
public class DefaultGUI {

    private final String QUIT_TEXT = "X";

    private JFrame jFrame;
    private Grappl grappl;
    private boolean isActuallyHash = false;
    private ConsoleGUI theConsoleWindow;
    private JLabel connectedClientsLabel;

    private ApplicationState applicationState;

    public DefaultGUI(final ApplicationState applicationState) {
        this.applicationState = applicationState;

        jFrame = new JFrame(Application.APP_NAME + " " + Application.VERSION);
        jFrame.setSize(new Dimension(310, 240));
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.setLayout(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JPasswordField jPasswordField = new JPasswordField("");
        final JCheckBox rememberMeBox = new JCheckBox();
        final DefaultGUI theGUI = this;

        final JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(5, 2, 250, 20);
        jFrame.add(usernameLabel);
        final JTextField usernameField = new JTextField("");
        usernameField.setBounds(5, 22, 250, 20);
        usernameField.setText(GrapplDataFile.getUsername());
        jFrame.add(usernameField);
        usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login(usernameField, jPasswordField, theGUI, rememberMeBox);
            }
        });

        final JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(5, 42, 250, 20);
        jFrame.add(passwordLabel);
        jPasswordField.setBounds(5, 62, 250, 20);
        jPasswordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login(usernameField, jPasswordField, theGUI, rememberMeBox);
            }
        });

        jPasswordField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                Application.usingSavedHashPass = false;
            }

            @Override
            public void keyPressed(KeyEvent e) {
                Application.usingSavedHashPass = false;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                Application.usingSavedHashPass = false;
            }
        });
        String password = GrapplDataFile.getPassword();
        jFrame.add(jPasswordField);
        if(password != null) {
            jPasswordField.setText(password);
            isActuallyHash = true;
        } else {
            Application.getLog().log("Password is null");
        }

        connectedClientsLabel = new JLabel("Waiting for connections");

        rememberMeBox.setBounds(10, 87, 20, 20);
        jFrame.add(rememberMeBox);
        final JLabel rememberMeLabel = new JLabel("Remember me");
        rememberMeLabel.setBounds(35, 87, 250, 20);
        jFrame.add(rememberMeLabel);
        if(isActuallyHash) {
            rememberMeBox.setSelected(true);
        } {
            final JButton logInButton = new JButton("Log in");
            logInButton.setBounds(4, 112, 140, 40);
            logInButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    login(usernameField, jPasswordField, theGUI, rememberMeBox);
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

                    jFrame = new JFrame(Application.APP_NAME + " ");
                    jFrame.setSize(new Dimension(300, 240));
                    jFrame.setLocation(wX, wY);
                    jFrame.setVisible(true);
                    jFrame.setLayout(null);
                    jFrame.setResizable(false);
                    jFrame.setSize(new Dimension(290, 230));
                    jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    jFrame.setIconImage(Application.getIcon());

                    JButton consoleButton = new JButton(QUIT_TEXT);
                    consoleButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            System.exit(0);
                        }
                    });
                    consoleButton.setBounds(235, 40, 40, 40);
                    jFrame.add(consoleButton);

                    try {
                        grappl = new GrapplBuilder().login("default", ("1".hashCode() + "").toCharArray(), jFrame)
                                .withGUI(theGUI).build();
                    } catch (AuthenticationException ignore) {
                        // TODO: Handle this.
                    }

                    JButton jButton = new JButton("Open console");
                    jButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                        if (theConsoleWindow == null) {
                            theConsoleWindow = new ConsoleGUI(applicationState);
                        } else {
                            theConsoleWindow.getConsoleFrame().toFront();
                        }
                        }
                    });
                    jFrame.add(jButton);
                    jButton.setBounds(2, 105, 280, 45);

                    JButton donateButton = new JButton("Donate");
                    donateButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                Desktop.getDesktop().browse(URI.create("http://grappl.io/donate"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                    donateButton.setBounds(2, 155, 280, 40);
                    jFrame.add(donateButton);

                    String ports = JOptionPane.showInputDialog("What port does your server run on?");
                    grappl.getInternalServer().setPort(Integer.parseInt(ports));

                    AdaptiveConnector adaptiveConnector = new AdaptiveConnector(applicationState.getRelayManager());
                    adaptiveConnector.subject(grappl);
                }
            });
            beAnonymousButton.setBounds(4, 155, 192, 40);
            jFrame.add(beAnonymousButton);

            JButton advancedButton = new JButton("Advanced");
            advancedButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jFrame.setVisible(false);
                    new AdvancedGUI(applicationState).create();
                }
            });
            advancedButton.setBounds(200, 155, 90, 40);
            jFrame.add(advancedButton);

            jFrame.setIconImage(Application.getIcon());
            jFrame.repaint();
        }
    }

    private void doConnection(Grappl grappl) {
        String relayToConnectTo = grappl.getAuthentication().getLocalizedRelayPrefix() + "." + Application.DOMAIN;
        try {
            grappl.connect(relayToConnectTo);
        } catch (RelayServerNotFoundException e1) {
            Application.getLog().log("Looks like the auth server just tried to send you to a crashed or non-existent relay.");
            Application.getLog().log("Attempting to connect you to a different relay...");

            String newRelay = "";
            if(relayToConnectTo.equalsIgnoreCase("n.grappl.io")) {
                newRelay = "s.grappl.io";
            } else if(relayToConnectTo.equalsIgnoreCase("s.grappl.io")) {
                newRelay = "n.grappl.io";
            } else if(relayToConnectTo.equalsIgnoreCase("e.grappl.io")) {
                newRelay = "n.grappl.io";
            } else if(relayToConnectTo.equalsIgnoreCase("p.grappl.io")) {
                newRelay = "s.grappl.io";
            }

            try {
                grappl.connect(newRelay);
            } catch (RelayServerNotFoundException e2) {
                Application.getLog().log("Alright, things are going disastrously wrong. " +
                        "You should contact @Cactose, half the servers are probably down.");
                e2.printStackTrace();
            }
        }
    }

    public JLabel getConnectedClientsLabel() {
        return connectedClientsLabel;
    }

    public void initializeGUI(String relayServerIP, String publicPort, int localPort) {
        final JTextPane label = new JTextPane();
        label.setContentType("text");
        label.setText("Public address: " + relayServerIP + ":" + publicPort);

        label.setBorder(null);
        label.setBackground(null);
        label.setEditable(false);
        label.setBounds(5, 8, 450, 20);
        getFrame().add(label);

        JLabel jLabel2 = new JLabel("Server on local port: " + localPort);
        jLabel2.setBounds(5, 25, 450, 20);
        getFrame().add(jLabel2);

        final JLabel dataTransferredLabel = new JLabel("Waiting for data");
        dataTransferredLabel.setBounds(5, 65, 450, 20);
        getFrame().add(dataTransferredLabel);

        getFrame().repaint();

        /* GUI update thread */
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    connectedClientsLabel.setText("Connected clients: " + grappl.getStatMonitor().getOpenConnections());
                    dataTransferredLabel.setText("Sent Data: " + grappl.getStatMonitor().getSentDataKB() + "KB - Recv Data: " + grappl.getStatMonitor().getReceivedKB() + "KB");
                    getFrame().repaint();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public JFrame getFrame() {
        return jFrame;
    }

    public void login(JTextField usernameField, JPasswordField jPasswordField, DefaultGUI theGUI, JCheckBox rememberMeBox) {

        GrapplBuilder grapplBuilder = applicationState.createGrapplBuilder(Protocol.TCP);

        String username = usernameField.getText().toLowerCase();
        char[] password = jPasswordField.getPassword();

        try {
            if(!isActuallyHash || !Application.usingSavedHashPass) {
                password = (new String(password).hashCode() + "").toCharArray();
            }

            grapplBuilder.login(username, password, jFrame).withGUI(theGUI);
            grappl = grapplBuilder.build();

            if(grappl.getAuthentication().isLoggedIn()) {
                Application.getLog().log("Logged in as " + grappl.getAuthentication().getUsername());
                Application.getLog().log("Beta tester: " + grappl.getAuthentication().isPremium());
                Application.getLog().log("Static port: " + grappl.getExternalServer().getPort());

                if(!rememberMeBox.isSelected()) {
                    password = null;
                }

                GrapplDataFile.saveUsername(grappl.getAuthentication().getUsername(), password);

                // options: nyc. sf. pac. lon. deu.
                String prefix = grappl.getAuthentication().getLocalizedRelayPrefix();

                int wX = jFrame.getX();
                int wY = jFrame.getY();

                jFrame.setVisible(false);

                JFrame newFrame = new JFrame(Application.APP_NAME + " Client ("+ grappl.getAuthentication().getUsername() + ")");
                // 300, 240
                newFrame.setSize(new Dimension(300, 240));
                newFrame.setLocation(wX, wY);
                newFrame.setResizable(false);
                newFrame.setSize(new Dimension(290, 230));
                newFrame.setIconImage(Application.getIcon());
                newFrame.setVisible(true);
                newFrame.setLayout(null);
                newFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                JButton jButton = new JButton("Close Grappl Client");
                jButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });
                newFrame.add(jButton);

                jButton.setBounds(2, 105, 280, 90);

                JButton consoleButton = new JButton("...");
                consoleButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                    if (theConsoleWindow == null) {
                        theConsoleWindow = new ConsoleGUI(applicationState);
                    } else {
                        theConsoleWindow.getConsoleFrame().toFront();
                    }
                    }
                });
                consoleButton.setBounds(235, 40, 40, 40);
                newFrame.add(consoleButton);

                String ports = JOptionPane.showInputDialog("What port does your server run on?");

                try {
                    Thread.sleep(330);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                jFrame = newFrame;

                grappl.getInternalServer().setPort(Integer.parseInt(ports));

                try {
                    AdaptiveConnector adaptiveConnector = new AdaptiveConnector(applicationState.getRelayManager());
                    adaptiveConnector.subject(grappl);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(getFrame(), "The value you entered is not a number");
                }
            } else {
                Application.getLog().log("Login failed!");
            }
        } catch (AuthenticationException authentication) {
        } catch (Exception esdfe) {
            esdfe.printStackTrace();
            Application.getLog().log("Yeah... that shouldn't have happened. Type the darn port next time!");
        }
    }

    public void destroyConsoleWindow() {
        theConsoleWindow = null;
    }
}
