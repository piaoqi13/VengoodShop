package com.vengood.util;

import android.util.Log;

/**
 *类名：EasyLogger.java
 *注释：打印Log输出工具
 *日期：2016年1月2日
 *作者：王超
 */
public class EasyLogger {
    public static boolean DEBUG_MODE = true;

    public static void e(String tag, String msg) {
        if (DEBUG_MODE) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG_MODE) {
            Log.e(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG_MODE) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (DEBUG_MODE) {
            Log.i(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG_MODE) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (DEBUG_MODE) {
            Log.d(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG_MODE) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (DEBUG_MODE) {
            Log.w(tag, msg, tr);
        }
    }
}
