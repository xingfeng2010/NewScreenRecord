package com.leauto.link.lightcar.server;

import android.content.Context;

import com.leauto.link.lightcar.AccesssoryManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class ConnectionThread extends Thread {
    private Socket socket;
    private MySocketServer socketServer;
    private Connection connection;
    private boolean isRunning;
    private Context mContext;

    public ConnectionThread(Socket socket, MySocketServer socketServer,Context context) {
        this.socket = socket;
        this.socketServer = socketServer;
        connection = new Connection(socket);
        isRunning = true;
        mContext = context;
    }

    @Override
    public void run() {
        while (isRunning) {
            // Check whether the socket is closed.
            if (socket.isClosed()) {
                isRunning = false;
                break;
            }

            BufferedReader reader;
            try {
//                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                String message = reader.readLine();
                AccesssoryManager.getAccesssoryManager(mContext).readDataFromStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMsg(byte[] data) {
//        socketServer.getMessageHandler().onSendMsg(connection);
        connection.sendData(data);
    }

    public void stopRunning() {
        isRunning = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}