package com.vk.mobile.util;

import android.test.AndroidTestCase;

/**
 * Created by myltik
 * Created on 7/19/13 5:16 PM
 */
public class HexFormatterUtilTest extends AndroidTestCase {

    public void testAsStringBytes() {
        byte[] data = new byte[] {0x11, 0x00, 0x07, 0x1E};
        String str = HexFormatterUtil.asString(data);

        assertEquals("0x11 0x00 0x07 0x1e ", str);
    }

    public void testAsStringInts() {
        int[] data = new int[] {0x11, 0x00, 0x07, 0x1E, 0xFF, 0xF7, 0xFF01, 0x01FA, 0x0001, 0xFFFF, 0x11EE, 0x11eeff};
        String str = HexFormatterUtil.asString(data);

        assertEquals("0x00000011 0x00000000 0x00000007 0x0000001e 0x000000ff 0x000000f7 0x0000ff01 0x000001fa 0x00000001 0x0000ffff 0x000011ee 0x0011eeff ", str);
    }

}
