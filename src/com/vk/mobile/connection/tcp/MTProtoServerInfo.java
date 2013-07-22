package com.vk.mobile.connection.tcp;

/**
 * Created by myltik
 * Created on 7/19/13 1:25 PM
 */
public class MTProtoServerInfo {

    protected final String host;
    protected final int port;

    public MTProtoServerInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "MTProtoServerInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
