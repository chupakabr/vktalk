package com.vk.mobile.connection.tcp;

/**
 * Created by myltik
 * Created on 7/19/13 1:25 PM
 */
public class MTProtoServerInfo {

    protected final String host;
    protected final int port;
    protected final String publicKey;

    public MTProtoServerInfo(String host, int port, String publicKey) {
        this.host = host;
        this.port = port;
        this.publicKey = publicKey;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPublicKey() {
        return publicKey;
    }

    @Override
    public String toString() {
        return "MTProtoServerInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
