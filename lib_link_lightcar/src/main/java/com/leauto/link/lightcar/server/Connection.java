package com.leauto.link.lightcar.server;

import android.util.Log;

import java.io.OutputStream;
import java.net.Socket;
import java.io.IOException;

public class Connection {
    private Socket socket;
    private OutputStream mOutStream;

    public Connection(Socket socket) {
        this.socket = socket;
        try {
            mOutStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(byte[] data) {
        try {
            Log.e("ScreenRecorder-fuck","========sendData======"+data.length);
            mOutStream.write(data);
            mOutStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        PrintWriter writer;
//        try {
//            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
//            writer.println(message);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}