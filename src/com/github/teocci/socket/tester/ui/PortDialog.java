package com.github.teocci.socket.tester.ui;

import javax.swing.*;
import java.awt.*;

import com.github.teocci.socket.tester.model.PortModel;
import com.github.teocci.socket.tester.util.Util;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-Jun-21
 */
public class PortDialog extends JDialog
{
    public static final int UDP = 1;
    public static final int TCP = 2;
    private PortModel model;

    /**
     * Creates a new instance of PortDialog
     */
    public PortDialog(JFrame parent, int type)
    {
        super(parent);
        if (type == TCP) {
            setTitle("Standard TCP Port");
            model = new PortModel("TCP-PORTS");
        } else {
            setTitle("Select UDP port");
            model = new PortModel("UDP-PORTS");
        }
        Container cp = getContentPane();

        JTable table = new JTable(model);
        cp.add(new JScrollPane(table));
        setSize(300, 200);
        Util.centerWindow(this);
    }

    public String getPort()
    {
        return model.getPort();
    }
}
