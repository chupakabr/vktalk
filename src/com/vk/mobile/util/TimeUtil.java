package com.vk.mobile.util;

/**
 * Created by myltik
 * Created on 7/19/13 1:29 PM
 */
public class TimeUtil {

    /**
     * @return Unixtime
     */
    public static int currentUnixtime() {
        return (int) (System.currentTimeMillis()/1000);
    }
}
