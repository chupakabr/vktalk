package com.vk.mobile.util;

import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by myltik
 * Created on 7/19/13 5:38 PM
 */
public class EndianConverterUtilTest extends AndroidTestCase {

    private static final String TAG = EndianConverterUtilTest.class.toString();

    public void testByteToIntArray() {
        byte[] data = new byte[] {0x11, 0x00, 0x07, 0x1E, 0x00, 0x7A, 0x11, 0x05};
        int[] res = EndianConverterUtil.byteToIntArray(data);
        int[] expected = new int[] {0x1E070011, 0x05117A00};

        Log.i(TAG, "expected:\t" + HexFormatterUtil.asString(expected));
        Log.i(TAG, "res:     \t" + HexFormatterUtil.asString(res));
        assertTrue(Arrays.equals(expected, res));
    }

    public void testIntToByteArray() {
        int[] data = new int[] {0x1100071E, 0x007A1105};
        byte[] res = EndianConverterUtil.intToByteArray(data);
        byte[] expected = new byte[] {0x1E, 0x07, 0x00, 0x11, 0x05, 0x11, 0x7A, 0x00};

        Log.i(TAG, "expected:\t" + HexFormatterUtil.asString(expected));
        Log.i(TAG, "res:     \t" + HexFormatterUtil.asString(res));
        assertTrue(Arrays.equals(expected, res));
    }
}
