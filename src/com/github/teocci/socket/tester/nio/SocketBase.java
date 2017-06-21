package com.github.teocci.socket.tester.nio;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-Jun-21
 */
abstract class SocketBase extends Thread
{
    SocketBase(String socketName)
    {
        super(socketName);
    }

    static String readInputStream(BufferedInputStream inputStream) throws IOException
    {
        String data = "";
        int s = inputStream.read();
        if (s == -1)
            return null;
        data += "" + (char) s;
        int len = inputStream.available();
        System.out.println("Len got : " + len);
        if (len > 0) {
            byte[] byteData = new byte[len];
            inputStream.read(byteData);
            data += new String(byteData);
        }
        return data;
    }
}
