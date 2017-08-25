package com.leauto.link.lightcar.aoacheck;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/5/18.
 */

public class AoaCheckList {
    private static String FILE_NAME = "aoa_check_list";

    public static void saveCheckResult(Context context,String model, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(model,value);
        editor.commit();
    }

    public static boolean containsPhone(Context context,String model) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.contains(model);
    }

    public static boolean getCheckResult(Context context,String model) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(model,false);
    }
}
