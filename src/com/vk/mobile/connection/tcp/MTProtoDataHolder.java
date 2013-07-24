package com.vk.mobile.connection.tcp;

import com.vk.mobile.util.EndianConverterUtil;
import com.vk.mobile.util.HexFormatterUtil;

import java.util.Arrays;

/**
 * Created by myltik
 * Created on 7/19/13 1:23 PM
 */
public class MTProtoDataHolder {

    protected final byte[] bytesData;
    protected final int[] data;
    protected final int payloadLength;

    public MTProtoDataHolder(byte[] data) {
        this(EndianConverterUtil.byteToIntArray(data));
    }

    public MTProtoDataHolder(int[] data) {
        this(data, data.length);
    }

    public MTProtoDataHolder(int[] data, int payloadLength) {
        this.data = data;
        this.payloadLength = payloadLength;
        this.bytesData = EndianConverterUtil.intToByteArray(data);
    }

    public boolean hasError() {
        return payloadLength == 1;
    }

    public int getErrorCode() {
        return data[0];
    }

    public int[] getData() {
        return data;
    }

    public byte[] getBytesData() {
        return bytesData;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public byte[] getBytes(int offset, int length) {
        assert bytesData.length >= offset+length;
        return Arrays.copyOfRange(bytesData, offset, offset+length);
    }

    public int[] getInts(int offset, int length) {
        assert data.length >= offset+length;
        return Arrays.copyOfRange(data, offset, offset+length);
    }

    @Override
    public String toString() {
        return "MTProtoDataHolder{" +
                "payloadLength=" + payloadLength +
                ",data("+data.length+")=" + HexFormatterUtil.asString(data) +
                ",bytesData("+bytesData.length+")=" + HexFormatterUtil.asString(bytesData) +
                '}';
    }
}
