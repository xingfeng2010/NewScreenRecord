package com.leauto.link.lightcar;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

/**
 * 检测手机横竖屏变化
 */
public class OriService extends Service {
    public OriService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 横竖屏变化通知
     *
     * @param paramConfiguration
     */
    public void onConfigurationChanged(Configuration paramConfiguration) {//响应系统方向改变
        AccesssoryManager accesssoryManager = AccesssoryManager.getAccesssoryManager(this);
        if (paramConfiguration.orientation == 2) {//横屏
            if (accesssoryManager != null) {
                accesssoryManager.setShuping(AccesssoryManager.LAND_DIRECTION);
            }
        } else {
            if (accesssoryManager != null) {
                accesssoryManager.setShuping(AccesssoryManager.PORTRAIT_DIRECTION);
            }
        }

        super.onConfigurationChanged(paramConfiguration);   //必有，否则运行会出现异常
    }
}
