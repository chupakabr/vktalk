package com.vk.mobile.connection.tcp;

import android.test.AndroidTestCase;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by myltik
 * Created on 7/23/13 11:12 PM
 */
public class AuthAsyncTaskTest extends AndroidTestCase {

    private static final String SERVER_PUBLIC_RSA_KEY = "-----BEGIN RSA PUBLIC KEY-----\n" +
            "MIIBCgKCAQEAwVACPi9w23mF3tBkdZz+zwrzKOaaQdr01vAbU4E1pvkfj4sqDsm6\n" +
            "lyDONS789sVoD/xCS9Y0hkkC3gtL1tSfTlgCMOOul9lcixlEKzwKENj1Yz/s7daS\n" +
            "an9tqw3bfUV/nqgbhGX81v/+7RFAEd+RwFnK7a+XYl9sluzHRyVVaTTveB2GazTw\n" +
            "Efzk2DWgkBluml8OREmvfraX3bkHZJTKX4EQSjBbbdJ2ZXIsRrYOXfaA+xayEGB+\n" +
            "8hdlLmAjbCVfaigxX0CDqWeR1yFL9kwd9P0NsZRPsmoqVwMbMu7mStFai6aIhc3n\n" +
            "Slv8kg9qv1m6XHVQY3PnEw+QQtqSIXklHwIDAQAB\n" +
            "-----END RSA PUBLIC KEY-----";

    public void testCrypto() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        String str = SERVER_PUBLIC_RSA_KEY;
//        str = str.replace("-----BEGIN RSA PUBLIC KEY-----\n", "");
//        str = str.replace("-----END RSA PUBLIC KEY-----", "");
//
//        final byte[] decode = Base64.decode(str, Base64.DEFAULT);
//        KeyFactory fact = KeyFactory.getInstance("RSA");
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decode);
//        PublicKey publicKeyObject = fact.generatePublic(keySpec);
//
//        Cipher cipher = Cipher.getInstance("RSA");
//        cipher.init(Cipher.ENCRYPT_MODE, publicKeyObject);
//        final byte[] res = cipher.doFinal(new byte[] {0x11});

        Security.addProvider(new BouncyCastleProvider());

        final PEMReader reader = new PEMReader(new StringReader(str));
        PublicKey publicKeyObject = (PublicKey) reader.readObject();

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKeyObject);
        final byte[] res = cipher.doFinal(new byte[] {0x11});

        assert res.length > 50;
    }
}
