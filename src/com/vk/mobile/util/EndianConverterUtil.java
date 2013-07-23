package com.vk.mobile.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by myltik
 * Created on 7/18/13 2:05 PM
 */
public class EndianConverterUtil {

    public static final ByteOrder DEFAULT_ORDER = ByteOrder.LITTLE_ENDIAN;
    public static final ByteOrder OPPOSITE_ORDER = ByteOrder.BIG_ENDIAN;

    public static int[] longToInts(long value) {
        return swap(new int[] {
                (int) (value & 0xFFFFFFFF),
                (int) (value >> 32 & 0xFFFFFFFF)
        });
    }

    public static byte[] intToBytes(int value, int length) {
        byte[] data = new byte[length];
        while (length-- > 0) {
            data[length] = (byte) (value >> (length*8) & 0xFF);
        }
        return data;
    }

    public static long bytesToLong(byte[] bytes) {
        return bytesToLong(bytes, OPPOSITE_ORDER);
    }

    public static long bytesToLong(byte[] bytes, ByteOrder order) {
        return ByteBuffer.wrap(bytes).order(order).getLong();
    }

    public static byte[] intToByteArray(int[] array) {
        return intToByteArray(array, DEFAULT_ORDER);
    }

    public static byte[] intToByteArray(int[] array, ByteOrder order) {
        ByteBuffer byteBuf = ByteBuffer.allocate(array.length*4);
        IntBuffer intBuf = byteBuf.asIntBuffer();
        intBuf.put(array);

        byte[] arrayBytes = ByteBuffer.wrap(byteBuf.array()).order(order).array();
        byte[] res = new byte[arrayBytes.length];
        for (int i = 0, j = -1; i < arrayBytes.length; i++) {
            if (j < 0) j = 3;
            res[i] = arrayBytes[(int)Math.floor(i/4d)*4+j--];
        }
        return res;
    }

    public static int[] byteToIntArray(byte[] array) {
        return byteToIntArray(array, DEFAULT_ORDER);
    }

    public static int[] byteToIntArray(byte[] array, ByteOrder order) {
        IntBuffer intBuf = ByteBuffer.wrap(array).order(order).asIntBuffer();
        int[] result = new int[intBuf.remaining()];
        intBuf.get(result);
        return result;
    }

    public static short swap(short value) {
        int b1 = value & 0xff;
        int b2 = (value >> 8) & 0xff;

        return (short) (b1 << 8 | b2 << 0);
    }

    public static int swap(int value) {
        int b1 = (value >>  0) & 0xff;
        int b2 = (value >>  8) & 0xff;
        int b3 = (value >> 16) & 0xff;
        int b4 = (value >> 24) & 0xff;

        return b1 << 24 | b2 << 16 | b3 << 8 | b4 << 0;
    }

    public static short[] swap(short[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = swap(array[i]);
        }
        return array;
    }

    public static int[] swap(int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = swap(array[i]);
        }
        return array;
    }
}
