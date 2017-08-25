package com.leauto.link.lightcar.server;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.leauto.link.lightcar.AccesssoryManager;
import com.leauto.link.lightcar.ThinCarDefine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ListeningThread extends Thread {
    private MySocketServer socketServer;
    private ServerSocket serverSocket;
    private boolean isRunning;
    private ConnectionThread connection;
    private Context mContext;
    private SocketConnectListener mSocketConnectListener;

    public interface SocketConnectListener {
        void onSocketAccepted();
    }

    public ListeningThread(MySocketServer socketServer, ServerSocket serverSocket,Context context, SocketConnectListener listener) {
        this.socketServer = socketServer;
        this.serverSocket = serverSocket;
        isRunning = true;
        mContext = context;
        mSocketConnectListener = listener;
    }

    public void sendmsg(byte[]data){
        if(connection!=null)
        {
            connection.sendMsg(data);
        }
    }

    @Override
    public void run() {
        while(isRunning) {
            Log.e("ScreenRecorder-fuck", "server isRunning:"+isRunning);
            if (serverSocket.isClosed()) {
                isRunning = false;
                break;
            }

            try {
                Socket socket = serverSocket.accept();
                Log.e("ScreenRecorder-fuck", "connected=======");

                if (connection != null) {
                    connection.stopRunning();
                }
                connection = new ConnectionThread(socket, socketServer,mContext);
                connection.start();

                mSocketConnectListener.onSocketAccepted();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopRunning() {
        isRunning = false;
    }

    public void stopCurrentConnection() {
        if (connection != null) {
            connection.stopRunning();
        }
    }
}