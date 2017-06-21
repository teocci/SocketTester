package com.github.teocci.socket.tester.nio;

import java.net.*;
import java.io.*;

import com.github.teocci.socket.tester.ui.SocketClientTest;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-Jun-21
 */
public class SocketClient extends SocketBase
{
    private static SocketClient socketClient = null;
    private Socket socket = null;
    private SocketClientTest parent;
    private BufferedInputStream in;
    private boolean disconnected = false;

    public synchronized void setDisconnected(boolean cr)
    {
        disconnected = cr;
    }

    private SocketClient(SocketClientTest parent, Socket s)
    {
        super("SocketClient");
        this.parent = parent;
        socket = s;
        setDisconnected(false);
        start();
    }

    public static synchronized SocketClient handle(SocketClientTest parent, Socket s)
    {
        if (socketClient == null)
            socketClient = new SocketClient(parent, s);
        else {
            if (socketClient.socket != null) {
                try {
                    socketClient.socket.close();
                } catch (Exception e) {
                    parent.error(e.getMessage());
                }
            }
            socketClient.socket = null;
            socketClient = new SocketClient(parent, s);
        }
        return socketClient;
    }

    public void run()
    {
        InputStream is;
        try {
            is = socket.getInputStream();
            in = new BufferedInputStream(is);
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException e2) {
                System.err.println("Socket not closed :" + e2);
            }
            parent.error("Could not open socket : " + e.getMessage());
            parent.disconnect();
            return;
        }

        while (!disconnected) {
            try {
                String got = readInputStream(in);
//                in.readLine();
                if (got == null) {
//                    parent.error("Connection closed by client");
                    parent.disconnect();
                    break;
                }
//                got = got.replaceAll("\n","<LF>");
//                got = got.replaceAll("\r","<CR>");
//                parent.append("R: "+got);
                parent.appendNoNewLine(got);
            } catch (IOException e) {
                if (!disconnected) {
                    parent.error(e.getMessage(), "Connection lost");
                    parent.disconnect();
                }
                break;
            }
        }
        try {
            is.close();
            in.close();
//            socket.close();
        } catch (Exception err) {}
        socket = null;
    }
}
