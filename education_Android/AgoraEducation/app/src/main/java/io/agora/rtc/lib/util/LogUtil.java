package io.agora.rtc.lib.util;

import android.util.Log;

public class LogUtil {

    private static String tagPre = "";

    public static void setTagPre(String tp) {
        tagPre = tp;
    }

    private String tag;

    public LogUtil(String tag) {
        this.tag = tagPre + tag;
    }

    public void d(String msg, Object... args) {
        Log.d(tag, String.format(msg, args));
    }

    public void i(String msg, Object... args) {
        Log.i(tag, String.format(msg, args));
    }

    public void w(String msg, Object... args) {
        Log.w(tag, String.format(msg, args));
    }

    public void e(String msg, Object... args) {
        Log.e(tag, String.format(msg, args));
    }

}
