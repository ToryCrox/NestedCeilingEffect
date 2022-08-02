package com.tory.nestedceiling.app.utils;

import android.annotation.SuppressLint;

import java.lang.reflect.Method;

/**
 * Created by zero on 2018/3/13.
 *
 * @author zero
 */

public class SystemProperties {

    private static Class<?> sClazz = null;
    private static Method sGetMethod = null;


    @SuppressLint("PrivateApi")
    private static Class<?> getClazz() throws ClassNotFoundException {
        return sClazz != null ? sClazz : Class.forName("android.os.SystemProperties");
    }

    @SuppressLint("PrivateApi")
    public static String get(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = getClazz();
            if (sGetMethod == null){
                sGetMethod = c.getMethod("get", String.class, String.class);
            }
            value = (String) (sGetMethod.invoke(c, key, defaultValue));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

}
