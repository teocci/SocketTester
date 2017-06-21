package com.github.teocci.socket.tester.util;

import javax.swing.*;
import java.net.*;
import java.io.*;
import java.awt.*;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-Jun-21
 */
public class Util
{
    public static void centerWindow(Window win)
    {
        Dimension dim = win.getToolkit().getScreenSize();
        win.setLocation(
                dim.width / 2 - win.getWidth() / 2,
                dim.height / 2 - win.getHeight() / 2
        );
    }

    public static boolean checkHost(String host)
    {
        try {
            InetAddress.getByName(host);
            return (true);
        } catch (UnknownHostException uhe) {
            return (false);
        }
    }

    public static void writeFile(String fileName, String text)
            throws IOException
    {
        PrintWriter out = new PrintWriter(
                new BufferedWriter(new FileWriter(fileName)));
        out.print(text);
        out.close();
    }

    public static String readFile(String fileName, Object parent)
            throws IOException
    {
        StringBuffer sb = new StringBuffer();
        ClassLoader cl = parent.getClass().getClassLoader();
        InputStream is = cl.getResourceAsStream("texts/"+fileName);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String s;
        while ((s = in.readLine()) != null) {
            sb.append(s);
            sb.append("\n");
        }
        in.close();
        return sb.toString();
    }

    public static int validateAddressAndPort(JPanel parent, JTextField ipField, JTextField portField)
    {
        String ip = ipField.getText();
        String port = portField.getText();
        if (ip == null || ip.equals("")) {
            JOptionPane.showMessageDialog(parent,
                    "No IP Address. Please enter IP Address",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            ipField.requestFocus();
            ipField.selectAll();
            return -1;
        }
        if (port == null || port.equals("")) {
            JOptionPane.showMessageDialog(parent,
                    "No Port number. Please enter Port number",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            portField.requestFocus();
            portField.selectAll();
            return -1;
        }
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (!Util.checkHost(ip)) {
            JOptionPane.showMessageDialog(parent,
                    "Bad IP Address",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            ipField.requestFocus();
            ipField.selectAll();
            parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return -1;
        }
        try {
            return Integer.parseInt(port);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Bad Port number. Please enter Port number",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            portField.requestFocus();
            portField.selectAll();
            parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return -1;
        }
    }
}
