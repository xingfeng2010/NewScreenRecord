package com.letv.leauto.ecolink.ui;

import android.content.Context;

import com.letv.leauto.ecolink.R;
import com.leauto.link.lightcar.IAOACallback;

/**
 * Created by Administrator on 2017/8/21.
 */

public class ThinCarIAOACallback implements IAOACallback{

    private Context mContext;

    public ThinCarIAOACallback(Context context) {
        mContext = context;
    }

    @Override
    public void onReceiveData(byte[] data) {

    }
}
