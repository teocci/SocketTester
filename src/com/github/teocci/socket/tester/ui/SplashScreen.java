package com.github.teocci.socket.tester.ui;

import java.awt.*;
import javax.swing.*;

import com.github.teocci.socket.tester.util.Util;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-Jun-21
 */
public class SplashScreen extends JWindow
{
    protected ImageIcon logo;
    protected JLabel productName;

    public SplashScreen()
    {
        ClassLoader cl = getClass().getClassLoader();
        logo = new ImageIcon(cl.getResource("icons/logo.png"));
        productName = new JLabel("<html><font face=\"Verdana\" size=10>" +
                "SocketTester v 3.0.0", logo, JLabel.CENTER);
        //productName.setBackground(Color.white);
        productName.setOpaque(true);

        productName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.black)));
        getContentPane().add(productName);
        Dimension dim = productName.getPreferredSize();
        dim.setSize(dim.getWidth() + 10, dim.getHeight() + 10);
        setSize(dim);
        Util.centerWindow(this);
        setVisible(true);
    }

    public void kill()
    {
        dispose();
    }
}
