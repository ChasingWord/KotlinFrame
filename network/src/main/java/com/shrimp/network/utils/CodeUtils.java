package com.shrimp.network.utils;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chasing
 */
public class CodeUtils {
    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 对字符串数组进行排序，按ASCII码从小到大
     */
    public static String[] orderByASCII(String[] keys) {
        for (int i = 0; i < keys.length - 1; i++) {
            for (int j = 0; j < keys.length - i - 1; j++) {
                String pre = keys[j];
                String next = keys[j + 1];
                if (isMoreThan(pre, next)) {
                    keys[j] = next;
                    keys[j + 1] = pre;
                }
            }
        }
        return keys;
    }

    private static boolean isMoreThan(String pre, String next) {
        if (null == pre || null == next || "".equals(pre) || "".equals(next)) {
            return false;
        }
        char[] c_pre = pre.toCharArray();
        char[] c_next = next.toCharArray();
        int minSize = Math.min(c_pre.length, c_next.length);

        for (int i = 0; i < minSize; i++) {
            if ((int) c_pre[i] > (int) c_next[i]) {
                return true;
            } else if ((int) c_pre[i] < (int) c_next[i]) {
                return false;
            }
        }
        return c_pre.length > c_next.length;
    }

    /**
     * 将Object对象里面的属性和值转化成Map对象
     */
    public static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        Object value;
        while (clazz != null) {
            for (Field field : clazz.getFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                try {
                    value = field.get(obj);
                    if (value != null && !"".equals(value.toString()))
                        map.put(fieldName, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }
        return map;
    }
}
