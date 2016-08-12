package io.grappl.gui;

import io.grappl.client.api.ClientConnection;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.LocationProvider;
import io.grappl.client.impl.*;
import io.grappl.client.impl.authentication.Authentication;
import io.grappl.client.impl.authentication.AuthenticationException;
import io.grappl.client.impl.authentication.AuthenticationPackets;
import io.grappl.client.impl.authentication.Authenticator;
import io.grappl.client.impl.relay.RelayServer;
import io.grappl.client.impl.error.RelayServerNotFoundException;
import io.grappl.client.impl.tcp.TCPGrappl;
import io.grappl.client.impl.event.UserConnectEvent;
import io.grappl.client.api.event.UserConnectListener;
import io.grappl.client.impl.event.UserDisconnectEvent;
import io.grappl.client.api.event.UserDisconnectListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * This is the GUI that appears when you click the 'Advanced' button right
 * after Grappl opens.
 */
public class AdvancedGUI {

    private JList<String> connectedUserList;
    private JLabel isLoggedInLabel;
    private JLabel premiumLabel;
    private String username;
    private char[] password;
    private boolean isActuallyHash;
    private JTextPane connectionLabel;
    private JLabel portLabel;
    private JButton logIn;
    private JButton signUpButton;
    private JButton donateButton;
    private JButton logOut;
    private JFrame jFrame;
    private JButton open;
    private JButton close;
    private JButton kickUsers;
    private JButton setReservedPortButton;

    public static JComboBox<String> relayServerDropdown;

    private ApplicationState applicationState;

    public AdvancedGUI(ApplicationState applicationState) {
        this.applicationState = applicationState;
    }

    public void create() {
        jFrame = new JFrame();
        jFrame.setIconImage(Application.getIcon());
        jFrame.setTitle("Grappl Advanced");
        jFrame.setSize(600, 300);
        jFrame.setLayout(null);
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JLabel relayLabel = new JLabel("Relay server");
        relayLabel.setBounds(20, 20, 100, 20);
        jFrame.add(relayLabel);

        if(Application.debugState) {
            if(System.getProperty("localadded").equals("false")) {
                relayServerDropdown.addItem("localhost (if you're testing)");
                System.setProperty("localadded", "true");
            }
        }

        relayServerDropdown.setBounds(20, 40, 200, 20);
        jFrame.add(relayServerDropdown);

        final JButton addRelay = new JButton("+");
        addRelay.setBounds(220, 40, 20, 20);
        addRelay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newRelay =
                        JOptionPane.showInputDialog(jFrame, "New relay server address");

                // If the user actually typed something...
                if(newRelay != null && !newRelay.equals("")) {
                    RelayServer relayServer = new RelayServer(newRelay, "User added");
                    relayServer.ping();

                    int index = relayServerDropdown.getItemCount();
                    relayServerDropdown.setSelectedIndex(index - 1);
                }
            }
        });
        jFrame.add(addRelay);

        final JLabel localPort = new JLabel("Local port");
        localPort.setBounds(20, 80, 200, 20);
        jFrame.add(localPort);

        final JTextField portTextField = new JTextField();
        portTextField.setBounds(20, 100, 130, 20);
        jFrame.add(portTextField);

        final JButton update = new JButton("Update");
        ActionListener portUpdate = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final int MAX_POSSIBLE_PORT_NUMBER = 65535;

                if (applicationState.getFocusedGrappl() != null) {
//                    new GraphGUI(applicationState.getFocusedGrappl().getStatMonitor());
                    try {
                        int portValue = Integer.parseInt(portTextField.getText());

                        if (portValue > MAX_POSSIBLE_PORT_NUMBER) {
                            JOptionPane.showConfirmDialog(jFrame, "Value too high. Port must be equal to or lower than " + MAX_POSSIBLE_PORT_NUMBER);
                        } else {
                            applicationState.getFocusedGrappl().getInternalServer().setPort(Integer.parseInt(portTextField.getText()));
                            portLabel.setText("Local port: " + applicationState.getFocusedGrappl().getInternalServer().getPort());
                        }
                    } catch (NumberFormatException ignore) {
                        JOptionPane.showConfirmDialog(jFrame,
                                "Value too high. Port must be equal to or lower than " + MAX_POSSIBLE_PORT_NUMBER);
                    }
                }
            }
        };
        update.addActionListener(portUpdate);
        portTextField.addActionListener(portUpdate);
        update.setBounds(150, 100, 90, 20);
        jFrame.add(update);

        close = new JButton("Close");
        open = new JButton("Open");
        open.setBounds(20, 140, 100, 40);
        final AdvancedGUI advancedGUI = this;
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (applicationState.getFocusedGrappl() == null) {
                    applicationState.addGrappl(new TCPGrappl(Application.getApplicationState()));
                    Grappl theGrappl = applicationState.getFocusedGrappl();

                    ((TCPGrappl) applicationState.getFocusedGrappl()).advancedGUI = advancedGUI;
                    applicationState.getFocusedGrappl().useAuthentication(applicationState.getAuthentication());
                    ((TCPGrappl) applicationState.getFocusedGrappl()).setInternalServerProvider(new LocationProvider() {
                        NetworkLocation location = new NetworkLocation("", -1);
                        @Override
                        public NetworkLocation getLocation() {
                            return location;
                        }
                    });

                    theGrappl.getInternalServer().setAddress(System.getProperty("serverhost"));
                    if(!portTextField.getText().equals("")) { // If field isn't empty
                        applicationState.getFocusedGrappl().getInternalServer().setPort(Integer.parseInt(portTextField.getText()));
                    }

                    boolean success;
                    try {
                        success = theGrappl.connect(((String) relayServerDropdown.getSelectedItem()).split("\\s+")[0]);
                    } catch (RelayServerNotFoundException e1) {
                        JOptionPane.showMessageDialog(null,
                                "Connection to relay server failed, it (or you!) may be offline.");
                        success = false;
                    }

                    if(success) {
                        theGrappl.addUserConnectListener(new UserConnectListener() {
                            @Override
                            public void userConnected(UserConnectEvent userConnectEvent) {
                                ((DefaultListModel<String>) connectedUserList.getModel())
                                        .addElement(userConnectEvent.getAddress());
                            }
                        });

                        theGrappl.addUserDisconnectListener(new UserDisconnectListener() {
                            @Override
                            public void userDisconnected(UserDisconnectEvent userDisconnectEvent) {
                                ((DefaultListModel<String>) connectedUserList.getModel())
                                        .removeElement(userDisconnectEvent.getAddress());
                            }
                        });
                        connectionLabel.setText("Public at: " + theGrappl.getExternalServer().getAddress() + ":" + theGrappl.getExternalServer().getPort());
                        jFrame.setTitle("Grappl Advanced | " + theGrappl.getExternalServer().getAddress() + ":" + theGrappl.getExternalServer().getPort());
                        portLabel.setText("Local port: " + theGrappl.getInternalServer().getPort());
                        close.setEnabled(true);
                    } else {
                        applicationState.removeGrappl(applicationState.getFocusedGrappl());
                    }
                } else {
                    JOptionPane.showMessageDialog(jFrame,
                            "Grappl connection already open! Close it before opening another.");
                }
            }
        });
        jFrame.add(open);

        close.setBounds(140, 140, 100, 40);
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applicationState.getFocusedGrappl().disconnect();
                connectionLabel.setText("Not connected - Tunnel closed");
                Application.getLog().log("Disconnected from relay");
                jFrame.setTitle("Grappl Advanced");
                applicationState.removeGrappl(applicationState.getFocusedGrappl());
                close.setEnabled(false);
            }
        });
        close.setEnabled(false);
        jFrame.add(close);

        int dist = 290;
        setReservedPortButton = new JButton("Set reserved/static port (Donator feature)");
        setReservedPortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int port = Integer.parseInt(JOptionPane.showInputDialog("New static port:"));
                    try {
                        boolean success = Application.getApplicationState().getAuthentication().changeReservedPortTo(port);
                        if (success)
                            JOptionPane.showMessageDialog(jFrame, "Success! Your static port has been set to: " + port + "\nLog out and log back in for it to take effect.");
                        else JOptionPane.showMessageDialog(jFrame, "Failed to set port. It may already be taken.");

                        premiumLabel.setText("Donator: true, reserved port: " + port);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } catch (Exception r) {}
            }
        });
        setReservedPortButton.setEnabled(false);
        setReservedPortButton.setBounds(dist, 105, 260, 20);
        jFrame.add(setReservedPortButton);

        kickUsers = new JButton("Kick connected users");
        kickUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(ClientConnection tcpClientConnection : Application.getApplicationState().getGrappl(0).getConnectedClients()) {
                    tcpClientConnection.close();
                }
            }
        });
        kickUsers.setBounds(dist, 135, 260, 20);
        jFrame.add(kickUsers);

        connectionLabel = new JTextPane();
        connectionLabel.setEditable(false);
        connectionLabel.setBorder(null);
        connectionLabel.setBackground(null);
        connectionLabel.setText("Not connected - Tunnel closed");
        connectionLabel.setBounds(20, 200, 200, 20);
        jFrame.add(connectionLabel);

        portLabel = new JLabel("Local port not set");
        portLabel.setBounds(20, 220, 200, 20);
        jFrame.add(portLabel);


        JButton openConsoleButton = new JButton("Open console");
        openConsoleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ConsoleGUI(applicationState);
            }
        });
        openConsoleButton.setBounds(dist, 200, 120, 30);
        jFrame.add(openConsoleButton);

//        connectedUserList = new JList<String>(new DefaultListModel<String>());
//        JScrollPane jScrollPane = new JScrollPane(connectedUserList);
//        jScrollPane.setBounds(dist, 130, 250, 60);
//        jFrame.add(jScrollPane);

        isLoggedInLabel = new JLabel();
        isLoggedInLabel.setText("Anonymous: Not logged in");
        isLoggedInLabel.setBounds(dist, 20, 250, 20);
        jFrame.add(isLoggedInLabel);

        premiumLabel = new JLabel();
        premiumLabel.setText("Donator: false");
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

                jFrame.setIconImage(Application.getIcon());

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

                isActuallyHash = false;
                if (password != null) {
                    jPasswordField.setText(password);
                    isActuallyHash = true;
                } else {
//                    Application.getLog().log("Password is null");
                }
                jFrame.add(jPasswordField);

                final JButton login = new JButton("Log in");
                login.setBounds(22, 102, 120, 40);
                login.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        prepareForLogin(usernameField, jPasswordField);

                        try {
                            if (!isActuallyHash || !Application.usingSavedHashPass) {
                                theGUI.password = Authenticator.formatPassword(new String(theGUI.password));
                            }

                            try {
                                Authentication authentication = Authenticator.login(username, theGUI.password);

                                applicationState.useAuthentication(authentication);

                                if (authentication.isLoggedIn()) {
                                    isLoggedInLabel.setText("Logged in as: " + username);

                                    if(authentication.isPremium()) {
                                        premiumLabel.setText("Donator: true, reserved port: " + authentication.getReservedPort());
                                        setReservedPortButton.setEnabled(true);
                                    }

                                    Application.getLog().log("Logged in as " + authentication.getUsername());
                                    Application.getLog().log("Donator: " + authentication.isPremium());
                                    Application.getLog().log("Reserved port: " + authentication.getReservedPort());

                                    setGUIStateLoggedIn();

                                    GrapplDataFile.saveUsername(authentication.getUsername(), theGUI.password);
                                }
                            } catch (AuthenticationException exp) {
                                JOptionPane.showMessageDialog(jFrame, "Login failed, incorrect username or password or broken connection.\nError message: " + exp.getMessage());
                                Application.getLog().log(exp.getMessage());
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
        signUpButton.setEnabled(false);
        jFrame.add(signUpButton);

        JButton back = new JButton("Back to home");
        back.setBounds(dist + 130, 200, 120, 30);
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.setVisible(false);
                jFrame.dispose();
                if(applicationState.getFocusedGrappl() != null) {
                    applicationState.getFocusedGrappl().disconnect();
                    applicationState.removeGrappl(applicationState.getFocusedGrappl());
                }
                new DefaultGUI(Application.getApplicationState());
            }
        });
        jFrame.add(back);

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
        donateButton.setEnabled(false);
        jFrame.add(donateButton);

        jFrame.repaint();
        jFrame.setVisible(true);
    }

    public void setGUIStateLoggedIn() {
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
        setReservedPortButton.setEnabled(false);
        donateButton.setBounds(290 + 180, 70, 80, 30);
        isLoggedInLabel.setText("Anonymous: Not logged in");
        premiumLabel.setText("Donator: false");
        jFrame.repaint();
    }

    public void triggerClosing() {
        applicationState.getFocusedGrappl().disconnect();
        open.setEnabled(true);
        close.setEnabled(false);
    }

    public void prepareForLogin(JTextField usernameField, JPasswordField jPasswordField) {
        username = usernameField.getText().toLowerCase();
        password = jPasswordField.getPassword();
    }

    public JFrame getFrame() {
        return jFrame;
    }
}
