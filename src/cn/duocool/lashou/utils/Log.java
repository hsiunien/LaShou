package cn.duocool.lashou.utils;

import android.content.Context;

import cn.duocool.lashou.CommDef;

/**
 * Created by hsiunien on 14-5-15.
 */
public class Log {
    public static boolean SHOWLINE = false;

    public static void d(String info) {
        if (CommDef.DEBUG) {
            android.util.Log.d("Info:", info);
            if (SHOWLINE) {
                getTraceInfo();
                android.util.Log.d("lineInfo",getTraceInfo());
                SHOWLINE = false;
            }
        }
    }

    public static void d(Object cxt, String info) {
        if (CommDef.DEBUG) {
            if (SHOWLINE) {
                getTraceInfo();
                SHOWLINE = false;
            }
        }
    }

    public static void i(Object cxt, String info) {
        if (CommDef.DEBUG) {
            android.util.Log.i(cxt.getClass().toString(), info);
        }
    }

    public static void e(Object cxt, String info) {
        if (CommDef.DEBUG) {
            android.util.Log.i(cxt.getClass().toString(), info);
        }
    }

    public static String getTraceInfo() {
        StringBuffer sb = new StringBuffer();
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        int stacksLen = stacks.length;
        for (int i = 2; i < stacksLen && stacksLen > 2; i++) {
            sb.append("class: ").append(stacks[i].getClassName())
                    .append("->").append(stacks[i].getMethodName())
                    .append(";line:").append(stacks[i].getLineNumber()).append("\n");
        }
        return sb.toString();
    }
}
