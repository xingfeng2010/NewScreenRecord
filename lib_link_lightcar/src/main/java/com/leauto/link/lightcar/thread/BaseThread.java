package com.leauto.link.lightcar.thread;

/**
 * Created by Administrator on 2017/5/2.
 */

public class BaseThread extends Thread {
    protected boolean isRunning = false;

    public void startThread() {
        isRunning = true;
        if (!this.isAlive()) {
            this.start();
        }
    }

    public void stopThread() {
        isRunning = false;
    }
}
