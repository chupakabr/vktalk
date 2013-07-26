package com.vk.mobile.connection.tcp;

import android.util.Log;
import com.vk.mobile.util.EndianConverterUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Keep in mind that this is not thread-safe implementation.
 *
 * Created by myltik
 * Created on 7/23/13 7:20 PM
 */
public class MTProtoDataBuilder {

    private static final int CHUNK_SIZE = 500; // TODO Decrease this value
    private static final float CAPACITY_FACTOR = 2f;

    protected final ByteOrder byteOrder;
    protected ByteBuffer buffer;
    protected int actualSize; // bytes

    public MTProtoDataBuilder() {
        this(EndianConverterUtil.DEFAULT_ORDER);
    }

    public MTProtoDataBuilder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
        buffer = allocate(CHUNK_SIZE);
        actualSize = 0;
    }

    public MTProtoDataHolder build() {
        return new MTProtoDataHolder(Arrays.copyOfRange(buffer.array(), 0, actualSize));
    }

    public MTProtoDataBuilder appendByte(byte val) {
        raiseIfNeeded(1);
        buffer.put(val);
        ++actualSize;
        return this;
    }

    public MTProtoDataBuilder appendShort(short val) {
        raiseIfNeeded(2);
        buffer.putShort(val);
        actualSize += 2;
        return this;
    }

    public MTProtoDataBuilder appendInt(int val) {
        raiseIfNeeded(4);
        buffer.putInt(val);
        actualSize += 4;
        return this;
    }

    public MTProtoDataBuilder appendLong(long val) {
        raiseIfNeeded(8);
        buffer.putLong(val);
        actualSize += 8;
        return this;
    }

    public MTProtoDataBuilder appendByteArray(byte[] array) {
        for (byte v : array) {
            appendByte(v);
        }
        return this;
    }

    public MTProtoDataBuilder appendShortArray(short[] array) {
        for (short v : array) {
            appendShort(v);
        }
        return this;
    }

    public MTProtoDataBuilder appendIntArray(int[] array) {
        for (int v : array) {
            appendInt(v);
        }
        return this;
    }

    public MTProtoDataBuilder appendLongArray(long[] array) {
        for (long v : array) {
            appendLong(v);
        }
        return this;
    }

    private void raiseIfNeeded(int extraBytes) {
        if (buffer.remaining()-extraBytes < 0) {
            ByteBuffer tmpBuf = allocate((int) (buffer.capacity() * CAPACITY_FACTOR));
            tmpBuf.put(buffer.array());
            buffer = tmpBuf;
        }
    }

    private ByteBuffer allocate(int capacity) {
        return ByteBuffer.allocate(capacity).order(byteOrder);
    }
}
