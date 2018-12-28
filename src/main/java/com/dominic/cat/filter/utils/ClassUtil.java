package com.dominic.cat.filter.utils;

/**
 * Create by dominic on 2018/12/26 15:14.
 */
public class ClassUtil {

    public static boolean isPresent(String className) {
        try {
            Class.forName(className);
            return true;
        }
        catch (Throwable ex) {
            // Class or one of its dependencies is not present...
            return false;
        }
    }
}
