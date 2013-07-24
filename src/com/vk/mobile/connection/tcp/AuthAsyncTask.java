package com.vk.mobile.connection.tcp;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import com.vk.mobile.crypto.CommonCryptoException;
import com.vk.mobile.util.EndianConverterUtil;
import com.vk.mobile.util.HexFormatterUtil;
import com.vk.mobile.util.MathUtil;
import com.vk.mobile.util.TimeUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by myltik
 * Created on 7/19/13 1:17 PM
 */
public class AuthAsyncTask extends AsyncTask<MTProtoServerInfo, Integer, MTProtoDataHolder> {

    private static final String TAG = AuthAsyncTask.class.toString();

    private MTProtoServerInfo serverInfo;

    private int[] prev128nonce;
    private int[] prev256nonce;

    private Pair<Integer, Integer> pqPrimeFactors;

    @Override
    protected MTProtoDataHolder doInBackground(MTProtoServerInfo... params) {
        Log.d(TAG, "Starting Authentication...");

        try {
            MTProtoDataHolder resp = doAuth(params[0]);

            Log.d(TAG, "FINAL RESPONSE: [" + HexFormatterUtil.asString(resp.getData()) + "]");
            Log.d(TAG, "Finish Authentication");

            return resp;
        } catch (CommonCryptoException e) {
            Log.e(TAG, "Finish Authentication with crypto error: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Authenticate to the server via MTProto
     * @param serverInfo
     * @return Last response
     */
    private MTProtoDataHolder doAuth(MTProtoServerInfo serverInfo) throws CommonCryptoException {
        this.serverInfo = serverInfo;
        MTProtoDataHolder resp = null;
        MTProtoSocket sock = null;

        try {
            sock = new MTProtoSocket(serverInfo.getHost(), serverInfo.getPort());

            resp = sendRequest("111 req_pq#60469778", sock, generatePQRequest());
            //resp = sendRequest("222 p_q_inner_data#83c95aec", sock, generatePQInnerDataRequest(resp));
            resp = sendRequest("333 req_DH_params#d712e4be", sock, generateDHParamsRequest(resp));
//            resp = sendRequest("444 server_DH_inner_data#b5890dba", sock, generateDHInnerDataRequest(resp));
//            resp = sendRequest("555 client_DH_inner_data#6643b654", sock, generateDHClientInnerDataRequest(resp));
//            resp = sendRequest("777 set_client_DH_params#f5045f1f", sock, generateDHClientSetParamsRequest(resp));

        } catch (IOException e) {
            Log.e(TAG, "Cannot read data from " + serverInfo + ": " + e.getMessage(), e);
        } finally {
            if (sock != null) {
                try {
                    sock.close();
                } catch (Throwable t) {
                    Log.e(TAG, "Cannot gracefully close socket: " + t.getMessage(), t);
                }
            }
        }

        return resp;
    }

    /**
     * Send request to the server via MTProto
     * @param flashTag
     * @param sock
     * @param request
     * @return Response
     * @throws IOException
     */
    private MTProtoDataHolder sendRequest(final String flashTag, final MTProtoSocket sock, final MTProtoDataHolder request)
            throws IOException
    {
        Log.d(TAG, flashTag + " Request " + request);

        MTProtoDataHolder resp = sock.sendRequest(request);
        Log.d(TAG, flashTag + " Response " + resp);

        if (resp.hasError()) {
            Log.e(TAG, flashTag + " Error in response: " + resp.getErrorCode());
        }

        return resp;
    }

    /**
     * Send req_pq#60469778 command
     * @return Response
     */
    private MTProtoDataHolder generatePQRequest() {
        Log.d(TAG, "Generating data in generatePQRequest()");

        prev128nonce = random128bit();

        final MTProtoDataBuilder builder = new MTProtoDataBuilder();
        // auth_key_id
        builder.appendInt(0x00000000)
        .appendInt(0x00000000)
        // message_id
        .appendInt(0x00000000)
        .appendInt(TimeUtil.currentUnixtime())
        // message_length
        .appendInt(0x14)//20
        // req_pq call
        .appendInt(0x60469778)
        // nonce
        .appendIntArray(prev128nonce);

        return builder.build();
//        return new MTProtoDataHolder(new int[] {
//                // auth_key_id
//                0x00000000,
//                0x00000000,
//                // message_id
//                0x00000000,
//                TimeUtil.currentUnixtime(),
//                // message_length
//                0x14,//20
//                // req_pq call
//                0x60469778,
//                // nonce
//                prev128nonce[0],
//                prev128nonce[1],
//                prev128nonce[2],
//                prev128nonce[3]
//        });
    }

    /**
     * Send p_q_inner_data#83c95aec command
     * @param prevResponse
     * @return Response
     */
    private MTProtoDataHolder generatePQInnerDataRequest(MTProtoDataHolder prevResponse) {
        Log.d(TAG, "Generating data in generatePQInnerDataRequest()");

        assert prevResponse != null;

        prev256nonce = random256bit();
        final int[] pq = prevResponse.getInts(14, 3);
        final int[] serverNonce = prevResponse.getInts(10, 4);

        pqPrimeFactors = getPQPrimeFactors(prevResponse.getBytes(56, 12));

        final MTProtoDataBuilder builder = new MTProtoDataBuilder();
        // method name
        builder.appendInt(0x83c95aec)
        // pq
        .appendIntArray(pq)
        // p len
        .appendByte((byte)0x04)
        // p value
        .appendInt(pqPrimeFactors.first)
        // p alignment
        .appendByteArray(new byte[] {0,0,0})
        // q len
        .appendByte((byte)0x04)
        // q value
        .appendInt(pqPrimeFactors.second)
        // q alignment
        .appendByteArray(new byte[] {0,0,0})
        // nonce
        .appendIntArray(prev128nonce)
        // server_nonce
        .appendIntArray(serverNonce)
        // new_nonce
        .appendIntArray(prev256nonce);

        return builder.build();
    }

    /**
     * Evaluate prime factors for specified number
     * @param pqBytes
     * @return Prime factors as a pair of Long values
     */
    private Pair<Integer, Integer> getPQPrimeFactors(byte[] pqBytes) {
        byte[] pqMeaningBytes = Arrays.copyOfRange(pqBytes, 1, 1 + 8);
        Log.d(TAG, "Meaning PQ bytes are " + HexFormatterUtil.asString(pqMeaningBytes));
        long pq = EndianConverterUtil.bytesToLong(pqMeaningBytes);

        Log.d(TAG, "Evaluating prime factors for PQ=" + pq + " (" + HexFormatterUtil.asHexString(pq) + ")...");
        Pair<Integer, Integer> factors = MathUtil.primeFactors(pq);
        Log.d(TAG, "Prime factors for " + pq + " are "
                + HexFormatterUtil.asHexString(factors.first)
                + " <> " + HexFormatterUtil.asHexString(factors.second));

        return factors;
    }

    /**
     * Send req_DH_params#d712e4be command
     * @param prevResponse
     * @return Response
     */
    private MTProtoDataHolder generateDHParamsRequest(MTProtoDataHolder prevResponse) throws CommonCryptoException {
        Log.d(TAG, "Generating data in generateDHParamsRequest()");

        assert prevResponse != null;

        final int[] publicKeyFingerprints = prevResponse.getInts(19, 2);
        final int[] serverNonce = prevResponse.getInts(10, 4);

        final MTProtoDataHolder pqInnerData = generatePQInnerDataRequest(prevResponse);
        final byte[] sha1pqInnerData = DigestUtils.sha1(pqInnerData.getBytesData());
        final byte[] encryptedData = encryptWithRSA(sha1pqInnerData, serverInfo.getPublicKey());

        Log.d(TAG, "Public key fingerprints length in bytes is " + publicKeyFingerprints.length*4);
        Log.d(TAG, "SHA1 PQ encrypted inner data length in bytes is " + sha1pqInnerData.length);
        Log.d(TAG, "RSA encrypted data length in bytes is " + encryptedData.length);

        final MTProtoDataBuilder builder = new MTProtoDataBuilder();
        // auth_key_id
        builder.appendInt(0x00000000)
        .appendInt(0x00000000)
        // message_id
        .appendInt(0x00000000)
        .appendInt(TimeUtil.currentUnixtime())
        // message_length
        .appendInt(320)
        // req_pq call
        .appendInt(0xD712E4BE)
        // nonce
        .appendIntArray(prev128nonce)
        // server_nonce
        .appendIntArray(serverNonce)
        // p len
        .appendByte((byte)0x04)
        // p value
        .appendInt(pqPrimeFactors.first)
        // p alignment
        .appendByteArray(new byte[] {0,0,0})
        // q len
        .appendByte((byte)0x04)
        // q value
        .appendInt(pqPrimeFactors.second)
        // q alignment
        .appendByteArray(new byte[] {0,0,0})
        // public_key_fingerprint
        .appendIntArray(publicKeyFingerprints)
        // encrypted data length
        .appendInt(0x001000FE) // 0x100 == 256
//        .appendByte((byte)0xFE)
//        .appendByte((byte)0x00)
//        .appendShort((short)0x100)
        // encrypted_data
        .appendByteArray(encryptedData);

        return builder.build();
    }

    /**
     * Encrypt specified data with RSA algo using provided RSA public key string (encoded)
     * @param data
     * @param publicKey
     * @return
     * @throws CommonCryptoException
     */
    private byte[] encryptWithRSA(byte[] data, String publicKey) throws CommonCryptoException {
        try {
            Security.addProvider(new BouncyCastleProvider());

            final PEMReader reader = new PEMReader(new StringReader(publicKey));
            PublicKey publicKeyObject = (PublicKey) reader.readObject();

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKeyObject);
            return cipher.doFinal(data);

        } catch (IOException e) {
            throw new CommonCryptoException(e);
        } catch (NoSuchPaddingException e) {
            throw new CommonCryptoException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new CommonCryptoException(e);
        } catch (BadPaddingException e) {
            throw new CommonCryptoException(e);
        } catch (IllegalBlockSizeException e) {
            throw new CommonCryptoException(e);
        } catch (InvalidKeyException e) {
            throw new CommonCryptoException(e);
        }
    }

    /**
     * Send server_DH_inner_data#b5890dba command
     * @param prevResponse
     * @return Response
     */
    private MTProtoDataHolder generateDHInnerDataRequest(MTProtoDataHolder prevResponse) {
        Log.d(TAG, "Generating data in generateDHInnerDataRequest()");

        assert prevResponse != null;
        // TODO

        final MTProtoDataBuilder builder = new MTProtoDataBuilder();
        return builder.build();
    }

    /**
     * Send client_DH_inner_data#6643b654 command
     * @param prevResponse
     * @return Response
     */
    private MTProtoDataHolder generateDHClientInnerDataRequest(MTProtoDataHolder prevResponse) {
        Log.d(TAG, "Generating data in generateDHClientInnerDataRequest()");

        assert prevResponse != null;
        // TODO

        final MTProtoDataBuilder builder = new MTProtoDataBuilder();
        return builder.build();
    }

    /**
     * Send set_client_DH_params#f5045f1f command
     * @param prevResponse
     * @return Response
     */
    private MTProtoDataHolder generateDHClientSetParamsRequest(MTProtoDataHolder prevResponse) {
        Log.d(TAG, "Generating data in generateDHClientSetParamsRequest()");

        assert prevResponse != null;
        // TODO

        final MTProtoDataBuilder builder = new MTProtoDataBuilder();
        return builder.build();
    }

    /**
     * Print int
     * @param flashTag
     * @param num
     */
    private void printNum(String flashTag, int num) {
        Log.v(TAG, String.format("%s [i]: %d - %x", flashTag, num, num));
    }

    /**
     * Print long
     * @param flashTag
     * @param num
     */
    private void printNum(String flashTag, long num) {
        Log.v(TAG, String.format("%s [l]: %dl - %x", flashTag, num, num));
    }

    /**
     * Generate random 128 bits
     * @return Random 128 bits, array of 4 ints
     */
    private int[] random128bit() {
        Random rnd = new Random(System.currentTimeMillis());
        return new int[] {rnd.nextInt(), rnd.nextInt(), rnd.nextInt(), rnd.nextInt()};
    }

    /**
     * Generate random 256 bits
     * @return Random 256 bits, array of 8 ints
     */
    private int[] random256bit() {
        Random rnd = new Random(System.currentTimeMillis());
        return new int[] {
                rnd.nextInt(), rnd.nextInt(), rnd.nextInt(), rnd.nextInt(),
                rnd.nextInt(), rnd.nextInt(), rnd.nextInt(), rnd.nextInt()
        };
    }
}
