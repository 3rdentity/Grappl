package io.grappl.client.impl.gui;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ConsoleGUI {

    public static JTextArea logDisplay;

    private Grappl grappl;
    private JFrame theFrame;

    public ConsoleGUI(final Grappl grappl) {
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
                    grappl.getGUI().destroyConsoleWindow();
                } catch (Exception ignore) {
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
            consoleFrame.setIconImage(Application.getIcon());
        } catch (Exception ee) {
            ee.printStackTrace();
        }

        consoleFrame.setLayout(null);
        consoleFrame.setSize(705, 350);
        consoleFrame.setLocationRelativeTo(null);
        consoleFrame.setVisible(true);

        logDisplay = new JTextArea(120, 20);
        logDisplay.setLineWrap(true);
        JScrollPane logDisplayScrollPane = new JScrollPane(logDisplay,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        logDisplayScrollPane.setVisible(true);
        logDisplayScrollPane.setBounds(10, 10, 670, 260);
        consoleFrame.add(logDisplayScrollPane);
        logDisplay.setEditable(false);
        logDisplay.setFont(new Font("Sans", Font.PLAIN, 12));

        final JTextField inputTypingArea = new JTextField();
        inputTypingArea.setBounds(20, 280, 530, 20);
        consoleFrame.add(inputTypingArea);
        inputTypingArea.setFocusable(true);
        inputTypingArea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String theCommand = inputTypingArea.getText();
                enterCommand(theCommand);
                inputTypingArea.setText(null);
            }
        });
        inputTypingArea.requestFocus();

        JButton enterLineButton = new JButton("Enter");
        enterLineButton.setBounds(570, 280, 80, 20);
        consoleFrame.add(enterLineButton);
        enterLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String theCommand = inputTypingArea.getText();
                enterCommand(theCommand);
                inputTypingArea.setText(null);
            }
        });

        // Secret message to get the console to send text to the window
        Application.getLog().log("PING");

        consoleFrame.repaint();
    }

    public boolean enterCommand(String command) {
        try {
            Application.getCommandHandler().handleCommand(grappl, command);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public JFrame getTheFrame() {
        return theFrame;
    }
}
