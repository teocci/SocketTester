package com.github.teocci.socket.tester.ui;

import java.awt.*;
import javax.swing.*;

import com.github.teocci.socket.tester.util.Util;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-Jun-21
 */
public class SocketTester extends JFrame
{
    private ClassLoader cl = getClass().getClassLoader();
    private ImageIcon logo = new ImageIcon(cl.getResource("icons/logo.png"));
    private ImageIcon ball = new ImageIcon(cl.getResource("icons/ball.png"));
    private JTabbedPane tabbedPane;

    /**
     * Creates a new instance of SocketTester
     */
    public SocketTester()
    {
        Container cp = getContentPane();

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        SocketClientTest client = new SocketClientTest(this);
        SocketServerTest server = new SocketServerTest(this);
        UdpTest udp = new UdpTest(this);
        About about = new About();

        tabbedPane.addTab("Client", ball, client, "Test any server");
        tabbedPane.addTab("Server", ball, server, "Test any client");
        tabbedPane.addTab("Udp", ball, udp, "Test any UDP Client or Server");
        tabbedPane.addTab("About", ball, about, "About SocketTester");

        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cp.add(tabbedPane);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ee) {
            System.out.println("Error setting native LAF: " + ee);
        }

        boolean toSplash = true;
        if (args.length > 0) {
            if ("nosplash".equals(args[0])) toSplash = false;
        }

        com.github.teocci.socket.tester.ui.SplashScreen splash = null;
        if (toSplash) splash = new com.github.teocci.socket.tester.ui.SplashScreen();

        SocketTester st = new SocketTester();
        st.setTitle("SocketTester v 3.0.0");
        st.setSize(600, 500);
        Util.centerWindow(st);
        st.setDefaultCloseOperation(EXIT_ON_CLOSE);
        st.setIconImage(st.logo.getImage());
        if (toSplash) splash.kill();
        st.setVisible(true);
    }

}
