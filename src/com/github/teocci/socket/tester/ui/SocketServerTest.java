package com.github.teocci.socket.tester.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.net.*;
import java.io.*;

import com.github.teocci.socket.tester.nio.SocketServer;
import com.github.teocci.socket.tester.util.Util;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-Jun-21
 */
public class SocketServerTest extends SocketBaseTest
{
    private JTextField ipField = new JTextField("0.0.0.0", 20);
    private JTextField portField = new JTextField("21", 10);

    private JButton connectButton = new JButton("Start Listening");
    private JButton disconnectButton = new JButton("Disconnect");

    private ServerSocket server;
    private SocketServer socketServer;

    protected final JFrame parent;

    public SocketServerTest(final JFrame parent)
    {
        //Container cp = getContentPane();
        this.parent = parent;
        Container cp = this;

        topPanel = new JPanel();
        toPanel = new JPanel();
        toPanel.setLayout(new GridBagLayout());
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        toPanel.add(ipLabel, gbc);

        gbc.weightx = 1.0; //streach
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ActionListener ipListener = e -> portField.requestFocus();
        ipField.addActionListener(ipListener);
        toPanel.add(ipField, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        toPanel.add(portLabel, gbc);

        gbc.weightx = 1.0;
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ActionListener connectListener = e -> connect();
        portField.addActionListener(connectListener);
        toPanel.add(portField, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        portButton.setMnemonic('P');
        portButton.setToolTipText("View Standard Ports");
        ActionListener portButtonListener = e -> {
            PortDialog dia = new PortDialog(parent, PortDialog.TCP);
            dia.show();
        };
        portButton.addActionListener(portButtonListener);
        toPanel.add(portButton, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        connectButton.setMnemonic('S');
        connectButton.setToolTipText("Start Listening");
        connectButton.addActionListener(connectListener);
        toPanel.add(connectButton, gbc);

        toPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Listen On"));
        topPanel.setLayout(new BorderLayout(10, 0));
        topPanel.add(toPanel);
        logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
        logoLabel.setHorizontalTextPosition(JLabel.CENTER);
        topPanel.add(logoLabel, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));


        textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout(0, 5));
        textPanel.add(convLabel, BorderLayout.NORTH);
        messagesField.setEditable(false);
        JScrollPane jsp = new JScrollPane(messagesField);
        textPanel.add(jsp);
        textPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));

        sendPanel = new JPanel();
        sendPanel.setLayout(new GridBagLayout());
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        sendPanel.add(sendLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        sendField.setEditable(false);
        sendPanel.add(sendField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        sendButton.setEnabled(false);
        sendButton.setToolTipText("Send text to client");
        ActionListener sendListener = e -> {
            String msg = sendField.getText();
            if (!msg.equals(""))
                sendMessage(msg);
            else {
                int value = JOptionPane.showConfirmDialog(
                        SocketServerTest.this, "Send Blank Line ?",
                        "Send Data To Client",
                        JOptionPane.YES_NO_OPTION);
                if (value == JOptionPane.YES_OPTION)
                    sendMessage(msg);
            }
        };
        sendButton.addActionListener(sendListener);
        sendField.addActionListener(sendListener);
        sendPanel.add(sendButton, gbc);
        ActionListener disconnectListener = e -> disconnect();
        gbc.gridx = 3;
        disconnectButton.addActionListener(disconnectListener);
        disconnectButton.setEnabled(false);
        sendPanel.add(disconnectButton, gbc);

        sendPanel.setBorder(
                new CompoundBorder(
                        BorderFactory.createEmptyBorder(0, 0, 0, 3),
                        BorderFactory.createTitledBorder("Send")));

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        gbc.weighty = 0.0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.BOTH;
        buttonPanel.add(sendPanel, gbc);
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        saveButton.setToolTipText("Save conversation with client to a file");
        saveButton.setMnemonic('S');
        ActionListener saveListener = e -> {
            String text = messagesField.getText();
            if (text.equals("")) {
                error("Nothing to save", "Save to file");
                return;
            }
            String fileName = "";
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            int returnVal = chooser.showSaveDialog(SocketServerTest.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileName = chooser.getSelectedFile().getAbsolutePath();
                try {
                    Util.writeFile(fileName, text);
                } catch (Exception ioe) {
                    JOptionPane.showMessageDialog(SocketServerTest.this,
                            "" + ioe.getMessage(),
                            "Error saving to file..",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        saveButton.addActionListener(saveListener);
        buttonPanel.add(saveButton, gbc);
        gbc.gridy = 1;
        clearButton.setToolTipText("Clear conversation with client");
        clearButton.setMnemonic('C');
        ActionListener clearListener = e -> messagesField.setText("");
        clearButton.addActionListener(clearListener);
        buttonPanel.add(clearButton, gbc);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 3));

        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(0, 10));
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        centerPanel.add(textPanel, BorderLayout.CENTER);

        CompoundBorder cb = new CompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 10, 10),
                connectedBorder);
        centerPanel.setBorder(cb);

        cp.setLayout(new BorderLayout(10, 0));
        cp.add(topPanel, BorderLayout.NORTH);
        cp.add(centerPanel, BorderLayout.CENTER);
    }

//    public static void main(String args[])
//    {
//        SocketServerTest stServer = new SocketServerTest();
//        stServer.setTitle("SocketTester Server");
//        stServer.setSize(500, 400);
//        Util.centerWindow(stServer);
//        stServer.setDefaultCloseOperation(EXIT_ON_CLOSE);
//        stServer.setIconImage(stServer.logo.getImage());
//        stServer.setVisible(true);
//    }

    @Override
    protected void connect()
    {
        if (server != null) {
            stop();
            return;
        }
        String ip = ipField.getText();
        String port = portField.getText();
        if (ip == null || ip.equals("")) {
            JOptionPane.showMessageDialog(SocketServerTest.this,
                    "No IP Address. Please enter IP Address",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            ipField.requestFocus();
            ipField.selectAll();
            return;
        }
        if (port == null || port.equals("")) {
            JOptionPane.showMessageDialog(SocketServerTest.this,
                    "No Port number. Please enter Port number",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            portField.requestFocus();
            portField.selectAll();
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (!Util.checkHost(ip)) {
            JOptionPane.showMessageDialog(SocketServerTest.this,
                    "Bad IP Address",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            ipField.requestFocus();
            ipField.selectAll();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        int portNo = 0;
        try {
            portNo = Integer.parseInt(port);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(SocketServerTest.this,
                    "Bad Port number. Please enter Port number",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            portField.requestFocus();
            portField.selectAll();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        try {
            InetAddress bindAddr;
            if (!ip.equals("0.0.0.0"))
                bindAddr = InetAddress.getByName(ip);
            else
                bindAddr = null;
            server = new ServerSocket(portNo, 1, bindAddr);

            ipField.setEditable(false);
            portField.setEditable(false);

            connectButton.setText("Stop Listening");
            connectButton.setMnemonic('S');
            connectButton.setToolTipText("Stop Listening");
//            sendButton.setEnabled(true);
//            sendField.setEditable(true);
        } catch (Exception e) {
            error(e.getMessage(), "Starting Server at " + portNo);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
//        changeBorder(" "+socket.getInetAddress().getHostName()+
//        	" ["+socket.getInetAddress().getHostAddress()+"] ");
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        messagesField.setText("> Server Started on Port: " + portNo + NEW_LINE);
        append("> ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        socketServer = SocketServer.handle(this, server);
//        sendField.requestFocus();
    }

    // Disconnect a client
    @Override
    public synchronized void disconnect()
    {
        try {
            socketServer.setDisconnected(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void stop()
    {
        try {
            disconnect(); //close any client
            socketServer.setStop(true);
        } catch (Exception e) {}
        server = null;
        ipField.setEditable(true);
        portField.setEditable(true);
        connectButton.setText("Start Listening");
        connectButton.setMnemonic('S');
        connectButton.setToolTipText("Start Listening");
        append("> Server stopped");
        append("> ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public synchronized void setClientSocket(Socket s)
    {
        if (s == null) {
            out = null;
            socket = null;
            changeBorder(null);
            sendButton.setEnabled(false);
            sendField.setEditable(false);
            disconnectButton.setEnabled(false);
        } else {
            socket = s;
            changeBorder(" " + socket.getInetAddress().getHostName() +
                    " [" + socket.getInetAddress().getHostAddress() + "] ");
            sendButton.setEnabled(true);
            sendField.setEditable(true);
            disconnectButton.setEnabled(true);
        }
    }
}
