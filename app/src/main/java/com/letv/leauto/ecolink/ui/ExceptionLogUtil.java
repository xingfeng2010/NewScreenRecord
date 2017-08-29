package com.letv.leauto.ecolink.ui;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/11/2.
 */
public class ExceptionLogUtil {
    private static ExceptionLogUtil ourInstance = new ExceptionLogUtil();
    // 用来存储设备信息和异常信息
    private Map<String, String> info = new HashMap<String, String>();
    // 用于格式化日期,作为日志文件名的一部分
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private ExecutorService mThreadPool = Executors.newFixedThreadPool(5);

    public static ExceptionLogUtil getInstance() {
        return ourInstance;
    }

    private ExceptionLogUtil() {

    }

    /**
     * 收集设备参数信息
     *
     */
    public void collectDeviceInfo(final Context context,final Throwable ex) {
        mThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    PackageManager pm = context.getPackageManager();// 获得包管理器
                    PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
                    if (pi != null) {
                        String versionName = pi.versionName == null ? "null" : pi.versionName;
                        String versionCode = pi.versionCode + "";
                        info.put("versionName", versionName);
                        info.put("versionCode", versionCode);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                Field[] fields = Build.class.getDeclaredFields();// 反射机制
                for (Field field : fields) {
                    try {
                        field.setAccessible(true);
                        info.put(field.getName(), field.get("").toString());
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                //保存日志文件
                saveCrashInfo2File(ex);
            }
        });

    }

    /**
     * 将异常信息保存至SD卡crash目录
     */
    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\r\n");
        }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        // 循环着把所有的异常信息写入writer中
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();// 记得关闭
        String result = writer.toString();
        sb.append(result);
        // 保存文件
        long timetamp = System.currentTimeMillis();
        String time = format.format(new Date());
        String fileName = "crash-" + time + "-" + timetamp + ".log";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File dir = new File(Environment.getExternalStorageDirectory(), "crash");
                if (!dir.exists())
                    dir.mkdir();

                File file = new File(dir, fileName);

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(sb.toString().getBytes());
                fos.close();
                return fileName;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
