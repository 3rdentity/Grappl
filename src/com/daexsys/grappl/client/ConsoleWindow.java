package com.daexsys.grappl.client;

import com.daexsys.grappl.GrapplGlobal;
import com.daexsys.grappl.client.commands.CommandHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;

public class ConsoleWindow {

    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;

    public static JTextArea display;

    public ConsoleWindow() {
        JFrame consoleFrame = new JFrame();
        consoleFrame.setTitle("Grappl Console");

        try {
            Client.authSocket = new Socket(GrapplGlobal.DOMAIN, GrapplGlobal.AUTHENTICATION);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataInputStream = new DataInputStream(Client.authSocket.getInputStream());
            dataOutputStream = new DataOutputStream(Client.authSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            consoleFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(new URL("http://grappl" +
                    ".io:888/html/glogo.png")));
        } catch (Exception ee) {
            ee.printStackTrace();
        }

        consoleFrame.setLayout(null);
        consoleFrame.setSize(705, 350);
        consoleFrame.setLocationRelativeTo(null);
        consoleFrame.setVisible(true);

//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (UnsupportedLookAndFeelException e) {
//            e.printStackTrace();
//        }

    display = new JTextArea(120,20);
        JScrollPane jScrollPane = new JScrollPane(display,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        ClientLog.log("Console window launched");

        jScrollPane.setVisible(true);
        jScrollPane.setBounds(10, 10, 670, 260);
        consoleFrame.add(jScrollPane);
        display.setEditable(false);
//
        final JTextField typeArea = new JTextField();
        typeArea.setBounds(20, 280, 230, 20);
        consoleFrame.add(typeArea);
//        typeArea.setFocusable(true);
//        typeArea.addKeyListener(new KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent e) {
////                System.out.println(e.getKeyLocation());
////                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
////                    final String theCommand = typeArea.getText();
////                    enterCommand(theCommand);
////                    typeArea.setText(null);
////                }
//            }
//
//            @Override
//            public void keyPressed(KeyEvent e) {
//
//            }
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//
//            }
//        });

        JButton jButton = new JButton("Enter");
        jButton.setBounds(270, 280, 80, 20);
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


        String output = "";

        CommandHandler.handleCommand(command, dataInputStream, dataOutputStream, Client.localPort);

//        ClientLog.log(output);
    }
}
