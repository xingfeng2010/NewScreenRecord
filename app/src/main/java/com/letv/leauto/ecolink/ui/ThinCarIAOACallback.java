package com.letv.leauto.ecolink.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.leauto.link.lightcar.MsgHeader;
import com.leauto.link.lightcar.ThinCarDefine;
import com.letv.leauto.ecolink.R;
import com.leauto.link.lightcar.IAOACallback;

/**
 * Created by Administrator on 2017/8/21.
 */

public class ThinCarIAOACallback implements IAOACallback{

    private Context mContext;

    @Override
    public void onReceiveData(MsgHeader headers, byte[] data) {
        processDataEvent(headers,data);
    }

    public ThinCarIAOACallback(Context context) {
        mContext = context;
    }

    private void processDataEvent(MsgHeader header, byte[] data) {
        switch (header.MsgCommand) {
            case ThinCarDefine.ProtocolFromCarCommand.COMMON_EVENT_COMMAND:
                if (header.MsgParam == ThinCarDefine.ProtocolFromCarParameter.AUTO_MSG_PARAM) {
                    handlerMsg(header.MsgParam, header.unknow, header.unknow1);
                }
                break;
        }
    }

    public void handlerMsg(int command, short params, short params2) {
        if (command == ThinCarDefine.ProtocolFromCarParameter.AUTO_MSG_PARAM) {
            switch (params) {
                case ThinCarDefine.ProtocolFromCarAction.START_SCREEN_RECORDER:
                case ThinCarDefine.ProtocolFromCarAction.STOP_SCREEN_RECORDER:
                case ThinCarDefine.ProtocolFromCarAction.RESTART_SCREEN_RECORDER:
                case ThinCarDefine.ProtocolFromCarAction.REQUEST_AOA_CHECK_RESULT:
                case ThinCarDefine.ProtocolFromCarAction.NOTIFY_AOA_CLICK_BEGIN:
                    break;
                default:
                    processCommandEvent(params);
                    break;
            }
        }
    }

    private void processCommandEvent(int command) {
        switch (command) {
            case ThinCarDefine.ProtocolFromCarAction.SHOW_NAVI:
                break;
            case ThinCarDefine.ProtocolFromCarAction.SHOW_LIVE:
                break;
            case ThinCarDefine.ProtocolFromCarAction.SHOW_LERADIO:
                mContext.startActivity(mContext.getPackageManager().getLaunchIntentForPackage("com.autonavi.minimap"));
                break;
            case ThinCarDefine.ProtocolFromCarAction.SHOW_SPEECH:
                break;
            case ThinCarDefine.ProtocolFromCarAction.SHOW_LERADIO_LOCAL:
                break;
        }
    }
}
