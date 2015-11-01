package io.grappl.client.gui;

import io.grappl.GrapplGlobals;
import io.grappl.client.Application;
import io.grappl.client.GrapplDataFile;
import io.grappl.client.api.Authentication;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.event.UserConnectEvent;
import io.grappl.client.api.event.UserConnectListener;
import io.grappl.client.api.event.UserDisconnectEvent;
import io.grappl.client.api.event.UserDisconnectListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URI;

/**
 * This is the GUI that appears when you click the 'Advanced' button right
 * after Grappl opens.
 */
public class AdvancedGUI {
    private JList<String> jList;

    private JLabel isLoggedInLabel;
    private JLabel premiumLabel;

    private String username;
    private char[] password;
    private boolean isActuallyHash;

    private Grappl focusedGrappl;
    private JLabel connectionLabel;
    private JLabel portLabel;

    private JButton logIn;
    private JButton signUpButton;
    private JButton donateButton;
    private JButton logOut;
    private JFrame jFrame;

    private JButton open;
    private JButton close;

    private Authentication activeAuthentication;

    public JFrame getFrame() {
        return jFrame;
    }

    public void create() {

        jFrame = new JFrame();

        jFrame.setIconImage(GrapplGlobals.getIcon());

        jFrame.setTitle("Grappl Advanced");

        jFrame.setVisible(true);
        jFrame.setSize(600, 300);
        jFrame.setLayout(null);
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JLabel relayLab = new JLabel("Relay server");
        relayLab.setBounds(20, 20, 100, 20);
        jFrame.add(relayLab);

        final JComboBox<String> jComboBox = new JComboBox<String>(
                new String[]{
                        "n.grappl.io (East Coast NA)",
                        "s.grappl.io (West Coast NA)",
                        "e.grappl.io (Europe)",
                        "p.grappl.io (Oceania)"
                }
        );
        if(GrapplGlobals.debugState) {
            jComboBox.addItem("localhost (if you're testing)");
        }

        jComboBox.setBounds(20, 40, 200, 20);
        jFrame.add(jComboBox);

        final JButton addRelay = new JButton("+");
        addRelay.setBounds(220, 40, 20, 20);
        addRelay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rServer = JOptionPane.showInputDialog(jFrame, "New relay server address");
                if(rServer != null && !rServer.equals("")) {
                    int index = jComboBox.getItemCount();
                    jComboBox.addItem(rServer);
                    jComboBox.setSelectedIndex(index);
                }
            }
        });
        jFrame.add(addRelay);

        final JLabel localPort = new JLabel("Local port");
        localPort.setBounds(20, 80, 200, 20);
        jFrame.add(localPort);

        final JTextField jTextField = new JTextField();
        jTextField.setBounds(20, 100, 130, 20);
        jFrame.add(jTextField);
        final JButton update = new JButton("Update");
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (focusedGrappl != null) {
                    try {
                        int portValue = Integer.parseInt(jTextField.getText());

                        if (portValue > 65535) {
                            JOptionPane.showConfirmDialog(jFrame, "Value too high. Port must be equal to or lower than 65535");
                        } else {
                            focusedGrappl.setInternalPort(Integer.parseInt(jTextField.getText()));
                            portLabel.setText("Local port: " + focusedGrappl.getInternalPort());
                        }
                    } catch (NumberFormatException ignore) {
                        JOptionPane.showConfirmDialog(jFrame, "Value too high. Port must be equal to or lower than 65535");
                    }
                }
            }
        });
        update.setBounds(150, 100, 90, 20);
        jFrame.add(update);

        close = new JButton("Close");
        open = new JButton("Open");
        open.setBounds(20, 140, 100, 40);
        final AdvancedGUI advancedGUI = this;
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (focusedGrappl == null) {
                    focusedGrappl = new Grappl();
                    focusedGrappl.aGUI = advancedGUI;
                    focusedGrappl.useAuthentication(activeAuthentication);
                    focusedGrappl.setInternalPort(Integer.parseInt(jTextField.getText()));

                    boolean success = focusedGrappl.connect(((String) jComboBox.getSelectedItem()).split("\\s+")[0]);

                    if(success) {
                        focusedGrappl.addUserConnectListener(new UserConnectListener() {
                            @Override
                            public void userConnected(UserConnectEvent userConnectEvent) {
                                ((DefaultListModel<String>) jList.getModel()).addElement(userConnectEvent.getAddress());
                            }
                        });

                        focusedGrappl.addUserDisconnectListener(new UserDisconnectListener() {
                            @Override
                            public void userDisconnected(UserDisconnectEvent userDisconnectEvent) {
                                ((DefaultListModel<String>) jList.getModel()).addElement(userDisconnectEvent.getAddress());
                            }
                        });
                        connectionLabel.setText("Public at: " + focusedGrappl.getRelayServer() + ":" + focusedGrappl.getExternalPort());
                        portLabel.setText("Local port: " + focusedGrappl.getInternalPort());
                        close.setEnabled(true);
                    } else {
                        focusedGrappl = null;
                    }
                } else {
                    JOptionPane.showMessageDialog(jFrame, "Grappl connection already open! Close it before opening another.");
                }
            }
        });
        jFrame.add(open);

        close.setBounds(140, 140, 100, 40);
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                focusedGrappl.disconnect();
                connectionLabel.setText("Not connected - Tunnel closed");
                Application.getClientLog().log("Disconnected..");
                focusedGrappl = null;
                close.setEnabled(false);
            }
        });
        close.setEnabled(false);
        jFrame.add(close);

        connectionLabel = new JLabel("Not connected - Tunnel closed");
        connectionLabel.setBounds(20, 200, 200, 20);
        jFrame.add(connectionLabel);

        portLabel = new JLabel("Local port not set");
        portLabel.setBounds(20, 220, 200, 20);
        jFrame.add(portLabel);

        int dist = 290;

        JButton jButton = new JButton("Open console");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ConsoleWindow(focusedGrappl);
            }
        });
        jButton.setBounds(dist, 200, 250, 30);
        jFrame.add(jButton);

        jList = new JList<String>(new DefaultListModel<String>());
        JScrollPane jScrollPane = new JScrollPane(jList);
        jScrollPane.setBounds(dist, 130, 250, 60);
        jFrame.add(jScrollPane);

        isLoggedInLabel = new JLabel();
        isLoggedInLabel.setText("Anonymous: Not logged in");
        isLoggedInLabel.setBounds(dist, 20, 250, 20);
        jFrame.add(isLoggedInLabel);

        premiumLabel = new JLabel();
        premiumLabel.setText("Beta tester: false");
        premiumLabel.setBounds(dist, 40, 250, 20);
        jFrame.add(premiumLabel);

        final AdvancedGUI theGUI = this;
        logIn = new JButton("Log in");
        logIn.setBounds(dist, 70, 80, 30);
        logIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final JFrame jFrame = new JFrame("Grappl Login");
                jFrame.setSize(new Dimension(320, 200));

                jFrame.setLocationRelativeTo(null);
                jFrame.setVisible(true);
                jFrame.setLayout(null);

                jFrame.setIconImage(GrapplGlobals.getIcon());

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
                final String password = GrapplDataFile.getPassword();
                jPasswordField.addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        GrapplGlobals.usingSavedHashPass = false;
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        GrapplGlobals.usingSavedHashPass = false;
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        GrapplGlobals.usingSavedHashPass = false;
                    }
                });

                isActuallyHash = false;
                if (password != null) {
                    jPasswordField.setText(password);
                    isActuallyHash = true;
                } else {
                    Application.getClientLog().log("Password is null");
                }
                jFrame.add(jPasswordField);

                final JButton login = new JButton("Log in");
                login.setBounds(22, 102, 120, 40);
                login.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Authentication authentication = new Authentication();
                        login(usernameField, jPasswordField);

                        try {
                            if (!isActuallyHash || !GrapplGlobals.usingSavedHashPass) {
                                theGUI.password = (new String(theGUI.password).hashCode() + "").toCharArray();
                            }

                            authentication.login(username, theGUI.password);
                            activeAuthentication = authentication;

                            if (authentication.isLoggedIn()) {
                                isLoggedInLabel.setText("Logged in as: " + username);

                                if(authentication.isPremium()) {
                                    premiumLabel.setText("Beta tester: true, static port: " + authentication.getStaticPort());
                                }

                                Application.getClientLog().log("Logged in as " + authentication.getUsername());
                                Application.getClientLog().log("Beta tester: " + authentication.isPremium());
                                Application.getClientLog().log("Static port: " + authentication.getStaticPort());
                                logIn();

                                GrapplDataFile.saveUsername(authentication.getUsername(), theGUI.password);
                            }
                        } catch (Exception ere) {
                            ere.printStackTrace();
                        }

                        jFrame.setVisible(false);
                    }
                });
                jFrame.add(login);

            }
        });
        jFrame.add(logIn);

        signUpButton = new JButton("Sign up");
        signUpButton.setBounds(dist + 90, 70, 80, 30);
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

        donateButton = new JButton("Donate");
        donateButton.setBounds(dist + 180, 70, 80, 30);
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
        jFrame.add(donateButton);

        jFrame.repaint();
    }

    public void logIn() {
        jFrame.remove(logIn);
        jFrame.remove(signUpButton);

        logOut = new JButton("Log out");
        logOut.setBounds(290, 70, 80, 30);
        logOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = null;
                password = null;
                logOut();
            }
        });
        jFrame.add(logOut);

        jFrame.add(logOut);
        donateButton.setBounds(290 + 90, 70, 80, 30);
        jFrame.repaint();
    }

    public void logOut() {
        jFrame.remove(logOut);
        jFrame.add(logIn);
        jFrame.add(signUpButton);
        donateButton.setBounds(290 + 180, 70, 80, 30);
        isLoggedInLabel.setText("Anonymous: Not logged in");
        premiumLabel.setText("Beta tester: false");
        jFrame.repaint();
    }

    public void triggerClosing() {
        focusedGrappl.disconnect();
        open.setEnabled(true);
        close.setEnabled(false);
    }

    public void login(JTextField usernameField, JPasswordField jPasswordField) {
        username = usernameField.getText().toLowerCase();
        password = jPasswordField.getPassword();
    }
}
