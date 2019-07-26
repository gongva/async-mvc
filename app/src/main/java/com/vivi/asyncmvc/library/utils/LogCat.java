package com.vivi.asyncmvc.library.utils;

import android.util.Log;

import java.util.Locale;

public class LogCat {
    public static String TAG = "HIK_VERSION";
    public static boolean debug = true;

    public LogCat() {
    }

    public static void setDebug(boolean debug) {
        debug = debug;
    }

    public static void v(String format, Object... args) {
        if (debug) {
            Log.v(TAG, buildMessage(format, args));
        }

    }

    public static void d(String format, Object... args) {
        if (debug) {
            Log.d(TAG, buildMessage(format, args));
        }

    }

    public static void i(String format, Object... args) {
        if (debug) {
            String message = buildMessage(format, args);
            Log.i(TAG, message);
        }
    }

    public static void w(String format, Object... args) {
        if (debug) {
            String message = buildMessage(format, args);
            Log.w(TAG, message);
        }
    }

    public static void e(String format, Object... args) {
        if (debug) {
            String message = buildMessage(format, args);
            Log.e(TAG, message);
        }

    }

    public static void e(Throwable err, String format, Object... args) {
        if (debug) {
            String message = buildMessage(format, args);
            Log.e(TAG, message);
        }

    }

    public static void wtf(String format, Object... args) {
        if (debug) {
            Log.wtf(TAG, buildMessage(format, args));
        }

    }

    public static void wtf(Throwable err, String format, Object... args) {
        if (debug) {
            Log.wtf(TAG, buildMessage(format, args), err);
        }

    }

    private static String buildMessage(String format, Object... args) {
        String msg = args == null ? format : String.format(Locale.US, format, args);
        StackTraceElement[] trace = (new Throwable()).fillInStackTrace().getStackTrace();
        String caller = "<unknown>";

        for(int i = 2; i < trace.length; ++i) {
            Class clazz = trace[i].getClass();
            if (!clazz.equals(LogCat.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf(46) + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf(36) + 1);
                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }

        return String.format(Locale.CHINA, "[%d] %s: %s", Thread.currentThread().getId(), caller, msg).replaceAll("\r\n", "");
    }
}
