package com.hebao.testkotlin.utils;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * 统一出错Handler
 */
public class CrashErrorHandler implements Thread.UncaughtExceptionHandler {

    private Application mContext;
    //用来存储设备信息和异常信息
    private final Map<String, String> info = new HashMap<>();
    private List<File> mRepeatFile;// 记录重复的奔溃文件，请求失败则只删除重复的，避免下次重复判断

    /**
     * 单例
     */
    private static CrashErrorHandler instance = null;

    private CrashErrorHandler() {
    }

    public static CrashErrorHandler getInstance() {
        if (instance == null)
            instance = new CrashErrorHandler();
        return instance;
    }

    /**
     * 初始化
     */
    public void init(Application application) {
        mContext = application;
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable ex) {
        //捕获信息
        collectDeviceInfo();
        collectErrorMsg(ex);
    }

    /**
     * 收集设备参数信息
     */
    private void collectDeviceInfo() {
        try {
            PackageManager pm = mContext.getPackageManager();// 获得包管理器
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
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
        Object obj;
        try {
            for (Field field : fields) {
                obj = field.get("");
                if (obj == null) continue;
                field.setAccessible(true);
                info.put(field.getName(), obj.toString());
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收集并打印错误信息到Logcat
     */
    private String collectErrorMsg(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\r\n");
        }
        Date date = new Date();
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance();
        String format = dateTimeInstance.format(date);
        sb.append(format).append("\r\n").append("Android报错信息:\r\n");
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        pw.close();// 记得关闭
        String result = writer.toString();
        //将奔溃日志打印到控制台，开发人员调试用
        sb.append(result);
        Log.e("exception", "uncaughtException errorReport=" + result);
        return sb.toString();
    }
}
