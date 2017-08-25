package com.leauto.link.lightcar.server;

import android.content.Context;

import java.io.IOException;
import java.net.ServerSocket;
import com.leauto.link.lightcar.server.ListeningThread.SocketConnectListener;

public class MySocketServer {
    private ServerSocket serverSocket;
    private ListeningThread listeningThread;

    private static MySocketServer instance;

    public static MySocketServer getInstance() {
        if (instance == null) {
            instance = new MySocketServer();
        }
        return instance;
    }

    public void initMySocketServer(int port, Context context,SocketConnectListener listener) {
        try {
            serverSocket = new ServerSocket(port);
            listeningThread = new ListeningThread(this, serverSocket,context,listener);
            listeningThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMsg(byte[] data)
    {
        listeningThread.sendmsg(data);
    }

    /*
     * Not ready for use.
     */
    public void close() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
//                listeningThread.suspend();
//                listeningThread.stop();
                serverSocket.close();
            }

            instance = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopCurrentConnection() {
        if (listeningThread != null) {
            listeningThread.stopCurrentConnection();
        }
    }
}