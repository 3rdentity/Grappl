package io.grappl.gui;

import io.grappl.client.impl.Application;
import io.grappl.client.impl.ApplicationState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ConsoleGUI {

    public static JTextArea logDisplay;

    private ApplicationState applicationState;
    private JFrame consoleFrame;

    public ConsoleGUI(final ApplicationState applicationState) {
        consoleFrame = new JFrame();
        this.applicationState = applicationState;
        consoleFrame.setResizable(false);

        consoleFrame.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    applicationState.getFocusedGrappl().getGUI().destroyConsoleWindow();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }

            public void windowClosed(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
            public void windowOpened(WindowEvent e) {}
        });
        consoleFrame.setTitle(Application.APP_NAME + " Ostia Console");

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
                Application.getLog().log("> " + theCommand);
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
                Application.getLog().log("> " + theCommand);
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
            Application.getCommandHandler().handleCommand(command);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public JFrame getConsoleFrame() {
        return consoleFrame;
    }
}
