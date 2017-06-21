package com.github.teocci.socket.tester.nio;

import java.net.*;

import com.github.teocci.socket.tester.ui.UdpTest;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-Jun-21
 */
public class UdpServer extends Thread
{
    public static int BUFFER_SIZE = 1024;

    private static UdpServer serverThread = null;
    // For listening for client responses
    private static UdpServer clientThread = null;


    private DatagramSocket server;
    private UdpTest parent;
    private boolean stop = false;
    private byte buffer[] = new byte[BUFFER_SIZE];

    // Stop server
    public synchronized void setStop(boolean cr)
    {
        stop = cr;
        if (server != null && cr == true) {
//            if (server instanceof MulticastSocket) {
//                MulticastSocket ms = (MulticastSocket) server;
//                ms.leaveGroup( ?);
//            }
            try {
                server.close();
            } catch (Exception e) {
                System.err.println("Error closing server : setStop : " + e);
            }
        }
    }

    private UdpServer(UdpTest parent, DatagramSocket s)
    {
        super("SocketUdp");
        this.parent = parent;
        server = s;
        setStop(false);
        start();
    }

    public static synchronized UdpServer handle(UdpTest parent, DatagramSocket s, boolean isServer)
    {
        UdpServer tempUdpServer = isServer ? serverThread : clientThread;

        if (tempUdpServer == null) {
            tempUdpServer = new UdpServer(parent, s);
        } else {
            if (tempUdpServer.server != null) {
                try {
                    tempUdpServer.setStop(true);
                    if (tempUdpServer.server != null)
                        tempUdpServer.server.close();
                } catch (Exception e) {
                    parent.error(e.getMessage());
                }
            }
            tempUdpServer.server = null;
            tempUdpServer = new UdpServer(parent, s);
        }

        if (isServer) {
            serverThread = tempUdpServer;
        } else {
            clientThread = tempUdpServer;
        }

        return isServer ? serverThread : clientThread;
    }

    public static synchronized UdpServer handleServer(UdpTest parent, DatagramSocket s)
    {
        return handle(parent, s, true);
    }

    public static synchronized UdpServer handleClient(UdpTest parent, DatagramSocket s)
    {
        return handle(parent, s, false);
    }

    public void run()
    {
        DatagramPacket pack = new DatagramPacket(buffer, buffer.length);
        while (!stop) {
            try {
                server.receive(pack);
                if (serverThread != null) {
                    if (server == serverThread.server) {
                        parent.append("R[" + pack.getAddress().getHostAddress() + ":"
                                + pack.getPort() + "]: " +
                                new String(pack.getData(), 0, pack.getLength()));
                    } else {
                        parent.append("R: " +
                                new String(pack.getData(), 0, pack.getLength()));
                    }
                } else {
                    parent.append("R: " + new String(pack.getData(), 0, pack.getLength()));
                }
            } catch (Exception e) {
                if (!stop) {
                    parent.error(e.getMessage(), "Error exception connection");
                    stop = true;
                }
            }
        }
    }
}
