package com.smilehacker.Megatron.util;

import android.util.Log;

import com.smilehacker.Megatron.BuildConfig;


/**
 * Created by kleist on 14-7-21.
 */
public class DLog {

    public final static int LOG_LEVEL_DEBUG = 0;
    public final static int LOG_LEVEL_INFO = 1;
    public final static int LOG_LEVEL_WARNING = 2;
    public final static int LOG_LEVEL_ERROR = 3;

    public static void i(String msg) {
        log(LOG_LEVEL_INFO, null, msg);
    }

    public static void d(String msg) {
        log(LOG_LEVEL_DEBUG, null, msg);
    }

    public static void e(String msg) {
        log(LOG_LEVEL_ERROR, null, msg);
    }

    public static void e(Throwable throwable) {
        log(LOG_LEVEL_ERROR, null, Log.getStackTraceString(throwable));
    }

    public static void w(String msg) {
        log(LOG_LEVEL_WARNING, null, msg);
    }

    public static void pwd() {
        log(LOG_LEVEL_INFO, null, " method call");
    }

    private static void log(int logLv, String tag, String msg) {
        if (!isDebug()) {
            return;
        }

        StackTraceElement[] elements = new Throwable().getStackTrace();
        if (elements != null && elements.length >= 2) {
            if (tag == null) {
                tag = getTag(elements[2]);
            }
            msg = getMsg(true, elements[2], msg);
        }
        tag = tag == null ? "" : tag;

        switch (logLv) {
            case LOG_LEVEL_DEBUG:
                Log.d(tag, msg);
                break;
            case LOG_LEVEL_INFO:
                Log.i(tag, msg);
                break;
            case LOG_LEVEL_WARNING:
                Log.w(tag, msg);
                break;
            case LOG_LEVEL_ERROR:
                Log.e(tag, msg);
                break;
        }

    }

    private static String getTag(StackTraceElement element) {
        String name = element.getFileName();
        if (name.endsWith(".java")) {
            name = name.substring(0, name.length() - 5);
        }
        return name;
    }

    private static String getMsg(boolean simple, StackTraceElement element, String msg) {
        String r;
        if (simple) {
            r = String.format("[%1$s:%2$s]\t%3$s",
                    element.getMethodName(), element.getLineNumber(), msg);
        } else {
            r = String.format(">>>>>>>>>>>%1$s<<<<<<<<<<\n[%2$s:%3$s]\t%4$s\n--------------------",
                    element.getClassName(), element.getMethodName(), element.getLineNumber(), msg);
        }
        return r;
    }


    private static Boolean isDebug() {
        return BuildConfig.DEBUG;
    }
}
