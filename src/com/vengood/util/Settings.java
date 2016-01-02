package com.vengood.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 *类名：Settings.java
 *注释：本地存储软件参数
 *日期：2016年1月2日
 *作者：王超
 */
public class Settings {
    public static final String SHARED_PREFERENCES_NAME = "PiaoqiStudio";
    public static SharedPreferences mSharedPreference = null;
    public static Editor mEditor = null;

    public static class BODY {
        public static final String BODY_ID = "body_id";
    }

    public static void initPreferences(Context context) {
        if (mSharedPreference == null) {
            mSharedPreference = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            mEditor = mSharedPreference.edit();
        }
    }

    private static String userId() {
        return mSharedPreference.getString("private", null);
    }

    public static void setString(String key, String value, boolean isPublic) {
        if (isPublic) {
            mEditor.putString(key, value);
        } else {
            mEditor.putString(userId() + key, value);
        }
        mEditor.commit();
    }

    public static String getString(String key, String defaultValue, boolean isPublic) {
        if (isPublic) {
            return mSharedPreference.getString(key, defaultValue);
        } else {
            return mSharedPreference.getString(userId() + key, defaultValue);
        }
    }

    public static void setBoolean(String key, boolean value, boolean isPublic) {
        if (isPublic) {
            mEditor.putBoolean(key, value);
        } else {
            mEditor.putBoolean(userId() + key, value);
        }
        mEditor.commit();
    }

    public static boolean getBoolean(String key, boolean defaultValue, boolean isPublic) {
        if (isPublic) {
            return mSharedPreference.getBoolean(key, defaultValue);
        } else {
            return mSharedPreference.getBoolean(userId() + key, defaultValue);
        }
    }

    public static void setInt(String key, int value, boolean isPublic) {
        if (isPublic) {
            mEditor.putInt(key, value);
        } else {
            mEditor.putInt(userId() + key, value);
        }
        mEditor.commit();
    }

    public static int getInt(String key, int defaultValue, boolean isPublic) {
        if (isPublic) {
            return mSharedPreference.getInt(key, defaultValue);
        } else {
            return mSharedPreference.getInt(userId() + key, defaultValue);
        }
    }

    public static void setFloat(String key, float value, boolean isPublic) {
        if (isPublic) {
            mEditor.putFloat(key, value);
        } else {
            mEditor.putFloat(userId() + key, value);
        }
        mEditor.commit();
    }

    public static float getFloat(String key, float defaultValue, boolean isPublic) {
        if (isPublic) {
            return mSharedPreference.getFloat(key, defaultValue);
        } else {
            return mSharedPreference.getFloat(userId() + key, defaultValue);
        }
    }

    public static void setLong(String key, long value, boolean isPublic) {
        if (isPublic) {
            mEditor.putLong(key, value);
        } else {
            mEditor.putLong(userId() + key, value);
        }
        mEditor.commit();
    }

    public static long getLong(String key, long defaultValue, boolean isPublic) {
        if (isPublic) {
            return mSharedPreference.getLong(key, defaultValue);
        } else {
            return mSharedPreference.getLong(userId() + key, defaultValue);
        }
    }
}
