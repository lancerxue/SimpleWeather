package com.example.dennis.jdtq.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences的封装
 * Created by dennis on 2017/6/26.
 */

public class SharePreUtils {
    public static SharedPreferences sp;
    public static final String SHARE_NAME="config";
    public static void putString(Context mContext, String key, String values){
        sp= mContext.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key,values).commit();
    }
    public static String getString(Context mContext, String key, String values){
        sp= mContext.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key,values);
    }
}
