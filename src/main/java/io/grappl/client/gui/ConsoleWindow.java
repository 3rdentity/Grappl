package io.grappl.client.gui;

import io.grappl.GrapplGlobals;
import io.grappl.client.ClientLog;
import io.grappl.client.GrapplClientState;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.CommandHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConsoleWindow {

    public static JTextArea display;

    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;

    private Grappl grappl;
    private JFrame theFrame;

    public ConsoleWindow(final Grappl grappl) {
        JFrame consoleFrame = new JFrame();
        theFrame = consoleFrame;
        this.grappl = grappl;
        consoleFrame.setResizable(false);
        theFrame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    grappl.getGui().theConsoleWindow = null;
                } catch (Exception ee) {
                }

            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        consoleFrame.setTitle("Grappl Console");

        try {
            CommandHandler.authSocket = new Socket(GrapplGlobals.DOMAIN, GrapplGlobals.AUTHENTICATION);
        } catch (IOException e) {
            ClientLog.log("Could not establish auth connection");
        }

        try {
            dataInputStream = new DataInputStream(CommandHandler.authSocket.getInputStream());
            dataOutputStream = new DataOutputStream(CommandHandler.authSocket.getOutputStream());
        } catch (Exception e) {
            ClientLog.log("Failed to create streams");
        }

        try {
            consoleFrame.setIconImage(GrapplClientState.getIcon());
        } catch (Exception ee) {
            ee.printStackTrace();
        }

        consoleFrame.setLayout(null);
        consoleFrame.setSize(705, 350);
        consoleFrame.setLocationRelativeTo(null);
        consoleFrame.setVisible(true);

        display = new JTextArea(120, 20);
        display.setLineWrap(true);
        JScrollPane jScrollPane = new JScrollPane(display,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        jScrollPane.setVisible(true);
        jScrollPane.setBounds(10, 10, 670, 260);
        consoleFrame.add(jScrollPane);
        display.setEditable(false);
//
        final JTextField typeArea = new JTextField();
        typeArea.setBounds(20, 280, 530, 20);
        consoleFrame.add(typeArea);
        typeArea.setFocusable(true);
        typeArea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String theCommand = typeArea.getText();
                enterCommand(theCommand);
                typeArea.setText(null);
            }
        });

        JButton jButton = new JButton("Enter");
        jButton.setBounds(570, 280, 80, 20);
        consoleFrame.add(jButton);
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String theCommand = typeArea.getText();
                enterCommand(theCommand);
                typeArea.setText(null);
            }
        });

        consoleFrame.repaint();
    }

    public void enterCommand(String command) {
        CommandHandler.handleCommand(grappl, command, dataInputStream, dataOutputStream);
    }

    public JFrame getTheFrame() {
        return theFrame;
    }
}
