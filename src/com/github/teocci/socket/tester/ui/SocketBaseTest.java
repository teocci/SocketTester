package com.github.teocci.socket.tester.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-Jun-21
 */
public class SocketBaseTest extends JPanel
{
    protected final String NEW_LINE = "\r\n";

    protected ClassLoader cl = getClass().getClassLoader();
    protected ImageIcon logo = new ImageIcon(cl.getResource("icons/logo.png"));

    protected JPanel topPanel;
    protected JPanel toPanel;
    protected JPanel centerPanel;

    protected JPanel textPanel;
    protected JPanel buttonPanel;
    protected JPanel sendPanel;

    protected JLabel ipLabel = new JLabel("IP Address");
    protected JLabel portLabel = new JLabel("Port");
    protected JLabel logoLabel = new JLabel("SocketTester v 3.0", logo, JLabel.CENTER);
    protected JLabel convLabel = new JLabel("Conversation with Client");

    protected JLabel sendLabel = new JLabel("Message");

    protected JButton portButton = new JButton("Port");
    protected JButton sendButton = new JButton("Send");
    protected JButton saveButton = new JButton("Save");
    protected JButton clearButton = new JButton("Clear");

    protected JTextArea messagesField = new JTextArea();

    protected JTextField sendField = new JTextField();

    protected Border connectedBorder = BorderFactory.createTitledBorder(new EtchedBorder(), "Connected Client : < NONE >");

    protected GridBagConstraints gbc = new GridBagConstraints();

    protected Socket socket;
    protected PrintWriter out;

    protected void connect() {}

    public synchronized void disconnect() {}

    public void error(String error)
    {
        if (error == null || error.equals("")) return;
        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void error(String error, String heading)
    {
        if (error == null || error.equals("")) return;
        JOptionPane.showMessageDialog(this, error, heading, JOptionPane.ERROR_MESSAGE);
    }

    public void append(String msg)
    {
        messagesField.append(msg + NEW_LINE);
        messagesField.setCaretPosition(messagesField.getText().length());
    }

    public void appendNoNewLine(String msg)
    {
        messagesField.append(msg);
        messagesField.setCaretPosition(messagesField.getText().length());
    }

    public void sendMessage(String s)
    {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            if (out == null) {
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
            }
            append("S: " + s);
            out.print(s + NEW_LINE);
            out.flush();
            sendField.setText("");
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } catch (Exception e) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Error Sending Message",
                    JOptionPane.ERROR_MESSAGE);
            disconnect();
        }
    }

    protected void changeBorder(String ip)
    {
        if (ip == null || ip.equals(""))
            connectedBorder = BorderFactory.createTitledBorder(
                    new EtchedBorder(), "Connected Client : < NONE >");
        else
            connectedBorder = BorderFactory.createTitledBorder(
                    new EtchedBorder(), "Connected Client : < " + ip + " >");
        CompoundBorder cb = new CompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 10, 10),
                connectedBorder);
        centerPanel.setBorder(cb);
        invalidate();
        repaint();
    }
}
