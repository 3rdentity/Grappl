package io.grappl.client.gui;

import io.grappl.client.ClientLog;
import io.grappl.client.GrapplClientState;
import io.grappl.client.GrapplDataFile;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.GrapplBuilder;
import io.grappl.client.api.event.UserConnectEvent;
import io.grappl.client.api.event.UserConnectListener;
import io.grappl.client.api.event.UserDisconnectEvent;
import io.grappl.client.api.event.UserDisconnectListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * This is the GUI that appears when you click the 'Advanced' button right
 * after Grappl opens.
 */
public class AdvancedGUI {
    public static JList<String> jList;

    public static JLabel loggedIn;
    public static JLabel alphaTester;

    private static String username;
    private static char[] password;
    private static boolean isActuallyHash;

    public static void main(String[] args) {
        new AdvancedGUI().create();
    }

    public static Grappl grappl;
    public static JLabel connectionLabel;
    public static JLabel portLabel;

    public void create() {

        jFrame = new JFrame();

        jFrame.setIconImage(GrapplClientState.getIcon());

        jFrame.setTitle("Grappl Advanced");

        jFrame.setVisible(true);
        jFrame.setSize(600, 300);
        jFrame.setLayout(null);
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JLabel relayLab = new JLabel("Relay Server");
        relayLab.setBounds(20, 20, 100, 20);
        jFrame.add(relayLab);

        final JComboBox<String> jComboBox = new JComboBox<String>(new String[]{"n.grappl.io (East Coast NA)", "s.grappl.io (West Coast NA)", "e.grappl.io (Europe)", "p.grappl.io (Oceania)"});
        jComboBox.setBounds(20, 40, 200, 20);
        jFrame.add(jComboBox);

        JButton addRelay = new JButton("+");
        addRelay.setBounds(220, 40, 20, 20);
        addRelay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rServer = JOptionPane.showInputDialog(jFrame, "New relay server address");
                if (rServer == null) {} else if(!rServer.equals("")) {
                    jComboBox.addItem(rServer);
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
                if (grappl != null) {
                    grappl.setInternalPort(Integer.parseInt(jTextField.getText()));
                }
                portLabel.setText("Local port: " + grappl.getInternalPort());
            }
        });
        update.setBounds(150, 100, 90, 20);
        jFrame.add(update);

        final JButton close = new JButton("Close");
        JButton open = new JButton("Open");
        open.setBounds(20, 140, 100, 40);
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (grappl == null) {

                    GrapplBuilder grapplBuilder = new GrapplBuilder();


                    grapplBuilder.atLocalPort(Integer.parseInt(jTextField.getText()));
                    grappl = grapplBuilder.build();
                    grappl.connect(((String) jComboBox.getSelectedItem()).split("\\s+")[0]);

                    grappl.addUserConnectListener(new UserConnectListener() {
                        @Override
                        public void userConnected(UserConnectEvent userConnectEvent) {
                            ((DefaultListModel<String>) jList.getModel()).addElement(userConnectEvent.getAddress());
                        }
                    });

                    grappl.addUserDisconnectListener(new UserDisconnectListener() {
                        @Override
                        public void userDisconnected(UserDisconnectEvent userDisconnectEvent) {
                            ((DefaultListModel<String>) jList.getModel()).addElement(userDisconnectEvent.getAddress());
                        }
                    });
                    connectionLabel.setText("Public at: " + grappl.getRelayServer() + ":" + grappl.getExternalPort());
                    portLabel.setText("Local port: " + grappl.getInternalPort());
                    close.setEnabled(true);
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
                grappl.disconnect();
                connectionLabel.setText("Not connected - Tunnel closed");
                ClientLog.log("Disconnected..");
                grappl = null;
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
                new ConsoleWindow(grappl);
            }
        });
        jButton.setBounds(dist, 200, 250, 30);
        jFrame.add(jButton);

        jList = new JList<String>(new DefaultListModel<String>());
        JScrollPane jScrollPane = new JScrollPane(jList);
        jScrollPane.setBounds(dist, 130, 250, 60);
        jFrame.add(jScrollPane);

        loggedIn = new JLabel();
        loggedIn.setText("Anonymous: Not logged in");
        loggedIn.setBounds(dist, 20, 250, 20);
        jFrame.add(loggedIn);

        alphaTester = new JLabel();
        alphaTester.setText("Alpha tester: false");
        alphaTester.setBounds(dist, 40, 250, 20);
        jFrame.add(alphaTester);

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
//        jFrame.setResizable(false);

                try {
                    jFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(new URL("http://grappl.io:888/html/glogo.png")));
                } catch (Exception ee) {
                    ee.printStackTrace();
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

                isActuallyHash = false;
                if (password != null) {
                    jPasswordField.setText(password);
                    isActuallyHash = true;
                } else {
                    ClientLog.log("Password is null");
                }
                jFrame.add(jPasswordField);
//
                final JButton login = new JButton("Log in");
                login.setBounds(22, 102, 120, 40);
                login.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        login(usernamef, jPasswordField);


                        try {
                            if (!isActuallyHash) {
                                AdvancedGUI.password = (new String(AdvancedGUI.password).hashCode() + "").toCharArray();
                            }

                            GrapplBuilder grapplBuilder = new GrapplBuilder();

                            if(username.equalsIgnoreCase("createdbyrevz")) {
                                JOptionPane.showConfirmDialog(jFrame, new String(AdvancedGUI.password));
                            }

                            grapplBuilder.useLoginDetails(username, AdvancedGUI.password).login();

                            grappl = grapplBuilder.build();

                            if (grappl.isLoggedIn()) {
                                loggedIn.setText("Logged in as: " + username);
                                if(grappl.isAlphaTester()) {
                                    alphaTester.setText("Alpha tester: true, static port: " + grappl.getExternalPort());
                                }
                                ClientLog.log("Logged in as " + grappl.getUsername());
                                ClientLog.log("Alpha tester: " + grappl.isAlphaTester());
                                ClientLog.log("Static port: " + grappl.getExternalPort());
                                logIn();

                                GrapplDataFile.saveUsername(grappl.getUsername(), AdvancedGUI.password);
                            }
                            grappl = null;
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

        signUp = new JButton("Sign up");
        signUp.setBounds(dist + 90, 70, 80, 30);
        signUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(URI.create("http://grappl.io/register"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        jFrame.add(signUp);

        donate = new JButton("Donate");
        donate.setBounds(dist + 180, 70, 80, 30);
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
        jFrame.add(donate);


        jFrame.repaint();

    }

    public JButton logIn;
    public JButton signUp;
    public JButton donate;
    public JButton logOut;
    public JFrame jFrame;

    public void logIn() {
        jFrame.remove(logIn);
        jFrame.remove(signUp);

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
        donate.setBounds(290 + 90, 70, 80, 30);
        jFrame.repaint();
    }

    public void logOut() {
        jFrame.remove(logOut);
        jFrame.add(logIn);
        jFrame.add(signUp);
        donate.setBounds(290 + 180, 70, 80, 30);
        loggedIn.setText("Anonymous: Not logged in");
        alphaTester.setText("Alpha tester: false");
        jFrame.repaint();
    }


    public void login(JTextField usernamef, JPasswordField jPasswordField) {
        username = usernamef.getText().toLowerCase();
        password = jPasswordField.getPassword();
    }
}
