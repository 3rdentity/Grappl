package io.grappl.client.impl.gui;

import io.grappl.client.api.Grappl;
import io.grappl.client.api.event.ConsoleMessageListener;
import io.grappl.client.impl.Application;
import io.grappl.client.api.ApplicationMode;
import io.grappl.client.impl.stable.ApplicationState;
import io.grappl.client.impl.stable.GrapplBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MultiLaunchGUI {

    private ApplicationState applicationState = new ApplicationState();
    private JList<Grappl> grapplJList;

    private JLabel grapplsOpenCount = new JLabel() {
        @Override
        public String getText() {
            try {
                return grapplJList.getModel().getSize() + " grappls open";
            } catch (Exception ignore) {}

            return "Oops! There was an error. Which should actually never happen.";
        }
    };

    public void addNewGrappl(Grappl grappl) {
        ((DefaultListModel<Grappl>) grapplJList.getModel()).addElement(grappl);
        applicationState.addGrappl(grappl);
    }

    public void removeGrappl(Grappl grappl) {
        ((DefaultListModel<Grappl>) grapplJList.getModel()).removeElement(grappl);
        applicationState.removeGrappl(grappl);
    }

    public int numberOfGrappls() {
        return Application.getApplicationState().getGrapplCount();
    }
//
//    public Grappl getGrapplByPort(int portNumber) {
//        for(Grappl grappl : Application.getApplicationState().getGrapplCount()) {
//            if(grappl.getInternalPort() == portNumber) {
//                return grappl;
//            }
//        }
//
//        return null;
//    }

    public void createWindow() {

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

        Application.create(null, ApplicationMode.NORMAL);
        JFrame jFrame = new JFrame("Multipl - (Grappl " + Application.VERSION + ")");
        jFrame.setSize(1280, 768);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.setIconImage(Application.getIcon());
        jFrame.setLayout(null);

        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        grapplJList = new JList<Grappl>(new DefaultListModel<Grappl>());
        grapplJList.setBounds(30, 80, 400, 600);
        jFrame.add(grapplJList);

        grapplsOpenCount.setBounds(40, 20, 400, 80);
        jFrame.add(grapplsOpenCount);

        JButton openNew = new JButton("Create");
        openNew.setBounds(30, 10, 140, 30);
        jFrame.add(openNew);

        JButton closeNew = new JButton("Destroy");
        closeNew.setBounds(200, 10, 140, 30);
        jFrame.add(closeNew);

        JButton editNew = new JButton("Edit");
        editNew.setBounds(370, 10, 140, 30);
        jFrame.add(editNew);

        JButton statsNew = new JButton("Stats");
        statsNew.setBounds(540, 10, 140, 30);
        jFrame.add(statsNew);

        int shiftX = 500;
        int shiftY = 380;

        final JTextArea display = new JTextArea(120, 20);
        display.setLineWrap(true);
        JScrollPane jScrollPane = new JScrollPane(display,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        jScrollPane.setVisible(true);
        jScrollPane.setBounds(shiftX + 10, shiftY + 10, 670, 260);
        jFrame.add(jScrollPane);
        display.setEditable(false);
        display.setFont(new Font("Sans", Font.PLAIN, 12));
//
        final JTextField typeArea = new JTextField();
        typeArea.setBounds(shiftX + 20, shiftY + 280, 530, 20);
        jFrame.add(typeArea);
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
        jButton.setBounds(shiftX + 570, shiftY + 280, 80, 20);
        jFrame.add(jButton);
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String theCommand = typeArea.getText();
                enterCommand(theCommand);
                typeArea.setText(null);
            }
        });
        jFrame.repaint();

        Application.getLog().addConsoleMessageListener(new ConsoleMessageListener() {
            @Override
            public void receiveMessage(String message) {
                display.append(message + "\n");
            }
        });

        for (int i = 0; i < 1; i++) {
            System.out.println(i);
            Grappl grappl = new GrapplBuilder().atLocalPort(i).build();
            grappl.setInternalPort(-1);
            grappl.connect("localhost");
            addNewGrappl(grappl);
        }

        jFrame.repaint();
    }

    public void enterCommand(String command) {}

    public static void main(String[] args) {
        new MultiLaunchGUI().createWindow();
    }
}
