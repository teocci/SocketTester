package com.github.teocci.socket.tester.nio;

import java.net.*;
import java.io.*;

import com.github.teocci.socket.tester.ui.SocketServerTest;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-Jun-21
 */
public class SocketServer extends SocketBase
{
    private static SocketServer socketServer = null;
    private Socket socket = null;
    private ServerSocket server = null;
    private SocketServerTest parent;
    private BufferedInputStream in;
    private boolean disconnected = false;
    private boolean stop = false;

    private SocketServer(SocketServerTest parent, ServerSocket s)
    {
        super("SocketServer");
        this.parent = parent;
        server = s;
        setStop(false);
        setDisconnected(false);
        start();
    }


    public static synchronized SocketServer handle(SocketServerTest parent, ServerSocket s)
    {
        if (socketServer == null)
            socketServer = new SocketServer(parent, s);
        else {
            if (socketServer.server != null) {
                try {
                    socketServer.setDisconnected(true);
                    socketServer.setStop(true);
                    if (socketServer.socket != null)
                        socketServer.socket.close();
                    if (socketServer.server != null)
                        socketServer.server.close();
                } catch (Exception e) {
                    parent.error(e.getMessage());
                }
            }
            socketServer.server = null;
            socketServer.socket = null;
            socketServer = new SocketServer(parent, s);
        }
        return socketServer;
    }

    public void run()
    {
        while (!stop) {
            try {
                socket = server.accept();
            } catch (Exception e) {
                if (!stop) {
                    parent.error(e.getMessage(), "Error acceptation connection");
                    stop = true;
                }
                continue;
            }
            startServer();
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                    System.err.println("Error closing client socket : " + e);
                }
                socket = null;
                parent.setClientSocket(socket);
            }
        }
    }

    private void startServer()
    {
        parent.setClientSocket(socket);
        InputStream is = null;
        parent.append("> New Client: " + socket.getInetAddress().getHostAddress());
        try {
            is = socket.getInputStream();
            in = new BufferedInputStream(is);
        } catch (IOException e) {
            parent.append("> Could not open input stream on Client " + e.getMessage());
            setDisconnected(true);
            return;
        }

        String rec;
        while (true) {
            rec = null;
            try {
                rec = readInputStream(in);//in.readLine();
            } catch (Exception e) {
                setDisconnected(true);
                if (!disconnected) {
                    parent.error(e.getMessage(), "Lost Client connection");
                    parent.append("> Server lost Client connection.");
                } else
                    parent.append("> Server closed Client connection.");
                break;
            }

            if (rec != null) {
//                rec = rec.replaceAll("\n","<LF>");
//                rec = rec.replaceAll("\r","<CR>");
//                parent.append("R: "+rec);
                parent.appendNoNewLine(rec);
            } else {
                setDisconnected(true);
                parent.append("> Client closed connection.");
                break;
            }
        }
    }

    // Disconnect client
    public synchronized void setDisconnected(boolean cr)
    {
        if (socket != null && cr == true) {
            try {
                socket.close();
            } catch (Exception e) {
                System.err.println("Error closing client : setDisconnected : " + e);
            }
        }
        disconnected = cr;
        //parent.setClientSocket(null);
    }

    // Stop server
    public synchronized void setStop(boolean cr)
    {
        stop = cr;
        if (server != null && cr == true) {
            try {
                server.close();
            } catch (Exception e) {
                System.err.println("Error closing server : setStop : " + e);
            }
        }
    }
}
