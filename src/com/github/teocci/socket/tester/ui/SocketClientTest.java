package com.github.teocci.socket.tester.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.security.SecureRandom;

import java.net.*;
import java.io.*;

import javax.net.ssl.*;

import com.github.teocci.socket.tester.CustomTrustManager;
import com.github.teocci.socket.tester.nio.SocketClient;
import com.github.teocci.socket.tester.util.Util;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-Jun-21
 */
public class SocketClientTest extends SocketBaseTest
{
    private JTextField ipField = new JTextField("127.0.0.1", 20);
    private JTextField portField = new JTextField("5000", 10);

    private JButton connectButton = new JButton("Connect");
    private JCheckBox secureButton = new JCheckBox("Secure");

    private boolean isSecure = false;

    private PrintWriter out;
    private SocketClient socketClient;
    protected final JFrame parent;

    public SocketClientTest(final JFrame parent)
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
        gbc.gridwidth = 4;
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
        connectButton.setMnemonic('C');
        connectButton.setToolTipText("Start Connection");
        connectButton.addActionListener(connectListener);
        toPanel.add(connectButton, gbc);


        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        secureButton.setToolTipText("Set Has Secure");
        secureButton.addItemListener(e -> isSecure = !isSecure);
        toPanel.add(secureButton, gbc);


        toPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Connect To"));
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
        sendButton.setToolTipText("Send text to host");
        ActionListener sendListener = e -> {
            String msg = sendField.getText();
            if (!msg.equals(""))
                sendMessage(msg);
            else {
                int value = JOptionPane.showConfirmDialog(
                        SocketClientTest.this, "Send Blank Line ?",
                        "Send Data To Server",
                        JOptionPane.YES_NO_OPTION);
                if (value == JOptionPane.YES_OPTION)
                    sendMessage(msg);
            }
        };
        sendButton.addActionListener(sendListener);
        sendField.addActionListener(sendListener);
        sendPanel.add(sendButton, gbc);
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
        saveButton.setToolTipText("Save conversation with host to a file");
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
            int returnVal = chooser.showSaveDialog(SocketClientTest.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileName = chooser.getSelectedFile().getAbsolutePath();
                try {
                    Util.writeFile(fileName, text);
                } catch (Exception ioe) {
                    JOptionPane.showMessageDialog(SocketClientTest.this,
                            "" + ioe.getMessage(),
                            "Error saving to file..",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        saveButton.addActionListener(saveListener);
        buttonPanel.add(saveButton, gbc);
        gbc.gridy = 1;
        clearButton.setToolTipText("Clear conversation with host");
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
//        SocketClientTest client = new SocketClientTest();
//        client.setTitle("SocketTester Client");
//        //client.pack();
//        client.setSize(500, 400);
//        Util.centerWindow(client);
//        client.setDefaultCloseOperation(EXIT_ON_CLOSE);
//        client.setIconImage(client.logo.getImage());
//        client.setVisible(true);
//    }

    @Override
    protected void connect()
    {
        if (socket != null) {
            disconnect();
            return;
        }
        String ip = ipField.getText();
        int portNo = Util.validateAddressAndPort(this, ipField, portField);

        try {
            if (isSecure == false) {
                System.out.println("Connectig in normal mode : " + ip + ":" + portNo);
                socket = new Socket(ip, portNo);
            } else {
                System.out.println("Connectig in secure mode : " + ip + ":" + portNo);
                //SocketFactory factory = SSLSocketFactory.getDefault();

                TrustManager[] tm = new TrustManager[]{new CustomTrustManager(SocketClientTest.this)};

                SSLContext context = SSLContext.getInstance("TLS");
                context.init(new KeyManager[0], tm, new SecureRandom());

                SSLSocketFactory factory = context.getSocketFactory();
                socket = factory.createSocket(ip, portNo);
            }

            ipField.setEditable(false);
            portField.setEditable(false);
            connectButton.setText("Disconnect");
            connectButton.setMnemonic('D');
            connectButton.setToolTipText("Stop Connection");
            sendButton.setEnabled(true);
            sendField.setEditable(true);
        } catch (Exception e) {
            e.printStackTrace();
            error(e.getMessage(), "Opening connection");
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        changeBorder(" " + socket.getInetAddress().getHostName() +
                " [" + socket.getInetAddress().getHostAddress() + "] ");
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        messagesField.setText("");
        socketClient = SocketClient.handle(this, socket);
        sendField.requestFocus();
    }

    @Override
    public synchronized void disconnect()
    {
        try {
            socketClient.setDisconnected(true);
            socket.close();
        } catch (Exception e) {
            System.err.println("Error closing client : " + e);
        }
        socket = null;
        out = null;
        changeBorder(null);
        ipField.setEditable(true);
        portField.setEditable(true);
        connectButton.setText("Connect");
        connectButton.setMnemonic('C');
        connectButton.setToolTipText("Start Connection");
        sendButton.setEnabled(false);
        sendField.setEditable(false);
    }
}
