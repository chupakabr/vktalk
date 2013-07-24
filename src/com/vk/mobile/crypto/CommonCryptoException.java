package com.vk.mobile.crypto;

/**
 * Created by myltik
 * Created on 7/23/13 10:54 PM
 */
public class CommonCryptoException extends Exception {

    public CommonCryptoException() {
    }

    public CommonCryptoException(String detailMessage) {
        super(detailMessage);
    }

    public CommonCryptoException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CommonCryptoException(Throwable throwable) {
        super(throwable);
    }
}
