package io.grappl.client.impl.gui;

import io.grappl.client.api.Grappl;
import io.grappl.client.api.Protocol;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.GrapplDataFile;
import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.stable.GrapplBuilder;
import io.grappl.client.impl.stable.RelayServerNotFoundException;
import io.grappl.client.impl.stable.tcp.TCPGrappl;

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

    private final String COMMAND_BUTTON_TEXT = "...";

    private JFrame jFrame;
    private Grappl grappl;
    private boolean isActuallyHash = false;
    private ConsoleGUI theConsoleWindow;
    private JLabel connectedClientsLabel;

    private ApplicationState applicationState;

    public DefaultGUI(final ApplicationState applicationState) {
        this.applicationState = applicationState;

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

        jFrame = new JFrame("Grappl " + Application.VERSION);
        jFrame.setSize(new Dimension(310, 240));
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.setLayout(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JLabel usernameLable = new JLabel("Username");
        usernameLable.setBounds(5, 2, 250, 20);
        jFrame.add(usernameLable);
        final JTextField usernameField = new JTextField("");
        usernameField.setBounds(5, 22, 250, 20);
        usernameField.setText(GrapplDataFile.getUsername());
        jFrame.add(usernameField);

        final JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(5, 42, 250, 20);
        jFrame.add(passwordLabel);
        final JPasswordField jPasswordField = new JPasswordField("");
        jPasswordField.setBounds(5, 62, 250, 20);
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

        final JCheckBox rememberMeBox = new JCheckBox();
        rememberMeBox.setBounds(10, 87, 20, 20);
        jFrame.add(rememberMeBox);
        final JLabel rememberMeLabel = new JLabel("Remember me");
        rememberMeLabel.setBounds(35, 87, 250, 20);
        jFrame.add(rememberMeLabel);

        final DefaultGUI theGUI = this;
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

                    JButton consoleButton = new JButton(COMMAND_BUTTON_TEXT);
                    consoleButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (theConsoleWindow == null) {
                                theConsoleWindow = new ConsoleGUI(applicationState);
                            } else {
                                theConsoleWindow.getTheFrame().toFront();
                            }
                        }
                    });
                    consoleButton.setBounds(235, 40, 40, 40);
                    jFrame.add(consoleButton);

                    grappl = new GrapplBuilder().login("default", ("1".hashCode() + "").toCharArray(), jFrame).withGUI(theGUI).build();

                    JButton jButton = new JButton("Close " + Application.APP_NAME + " Client");
                    jButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            System.exit(0);
                        }
                    });
                    jFrame.add(jButton);
                    jButton.setBounds(2, 105, 280, 90);

                    String ports = JOptionPane.showInputDialog("What port does your server run on?");
                    ((TCPGrappl) grappl).getInternalServer().setPort(Integer.parseInt(ports));

                    try {
                        grappl.connect(grappl.getAuthentication().getLocalizedRelayPrefix() + "." + Application.DOMAIN);
                    } catch (RelayServerNotFoundException e1) {
                        e1.printStackTrace();
                    }
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

                String domain = prefix + "." + Application.DOMAIN;

                int wX = jFrame.getX();
                int wY = jFrame.getY();

                jFrame.setVisible(false);

                JFrame newJframe = new JFrame(Application.APP_NAME + " Client ("+ grappl.getAuthentication().getUsername() + ")");
                // 300, 240
                newJframe.setSize(new Dimension(300, 240));
                newJframe.setLocation(wX, wY);
                newJframe.setResizable(false);
                newJframe.setSize(new Dimension(290, 230));
                newJframe.setIconImage(Application.getIcon());
                newJframe.setVisible(true);
                newJframe.setLayout(null);
                newJframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                JButton jButton = new JButton("Close " + Application.APP_NAME + " Client");
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
                            theConsoleWindow = new ConsoleGUI(applicationState);
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

                ((TCPGrappl) grappl).getInternalServer().setPort(Integer.parseInt(ports));
                try {
                    grappl.connect(domain);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(getFrame(), "The value you entered is not a number");
                }
            } else {
                Application.getLog().log("Login failed!");
            }
        } catch (Exception esdfe) {
            esdfe.printStackTrace();
            Application.getLog().log("Yeah... that shouldn't have happened. Type the darn port next time!");
        }
    }

    public void destroyConsoleWindow() {
        theConsoleWindow = null;
    }
}
