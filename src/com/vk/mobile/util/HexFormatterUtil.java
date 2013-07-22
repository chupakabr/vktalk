package com.vk.mobile.util;

/**
 * Created by myltik
 * Created on 7/19/13 3:52 PM
 */
public class HexFormatterUtil {

    /**
     * Build string representation of array of integers
     * @param array Array of integers
     * @return String representation of array
     * @todo Rewrite as it's very slow because of using String.format(), try Hex.encodeHexString() or similar
     */
    public static String asString(int[] array) {
        if (array == null || array.length <= 0) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        for (int i : array) {
            sb.append(String.format("0x%08x ", i));
        }

        return sb.toString();
    }

    /**
     * Build string representation of array of bytes
     * @param array Array of bytes
     * @return String representation of array
     * @todo Rewrite as it's very slow because of using String.format(), try Hex.encodeHexString() or similar
     */
    public static String asString(byte[] array) {
        if (array == null || array.length <= 0) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(String.format("0x%02x ", b));
        }

        return sb.toString();
    }

    /**
     * Build string representation of array of chars
     * @param array Array of chars
     * @return String representation of array
     * @todo Rewrite as it's very slow because of using String.format(), try Hex.encodeHexString() or similar
     */
    public static String asString(char[] array) {
        if (array == null || array.length <= 0) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        for (char c : array) {
            sb.append(String.format("0x%04x ", (short)c));
        }

        return sb.toString();
    }
}
