package com.vk.mobile.connection.tcp;

/**
 * Created by myltik
 * Created on 7/19/13 1:23 PM
 */
public class MTProtoDataHolder {

    protected final int[] data;

    public MTProtoDataHolder(int[] data) {
        this.data = data;
    }

    public int[] getData() {
        return data;
    }
}
