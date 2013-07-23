package com.vk.mobile.connection.tcp;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import com.vk.mobile.util.EndianConverterUtil;
import com.vk.mobile.util.HexFormatterUtil;
import com.vk.mobile.util.MathUtil;
import com.vk.mobile.util.TimeUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by myltik
 * Created on 7/19/13 1:17 PM
 */
public class AuthAsyncTask extends AsyncTask<MTProtoServerInfo, Integer, MTProtoDataHolder> {

    private static final String TAG = AuthAsyncTask.class.toString();

    private int[] prev128nonce;
    private int[] prev256nonce;

    private Pair<Long, Long> pqPrimeFactors;

    @Override
    protected MTProtoDataHolder doInBackground(MTProtoServerInfo... params) {
        Log.d(TAG, "Starting Authentication...");

        MTProtoDataHolder resp = doAuth(params[0]);

        Log.d(TAG, "FINAL RESPONSE: [" + HexFormatterUtil.asString(resp.getData()) + "]");
        Log.d(TAG, "Finish Authentication");

        return resp;
    }

    /**
     * Authenticate to the server via MTProto
     * @param serverInfo
     * @return Last response
     */
    private MTProtoDataHolder doAuth(MTProtoServerInfo serverInfo) {
        MTProtoDataHolder resp = null;
        MTProtoSocket sock = null;

        try {
            sock = new MTProtoSocket(serverInfo.getHost(), serverInfo.getPort());

            resp = sendRequest("111 req_pq#60469778", sock, generatePQRequest());
            resp = sendRequest("222 p_q_inner_data#83c95aec", sock, generatePQInnerDataRequest(resp));
//            resp = sendRequest("333 req_DH_params#d712e4be", sock, generateDHParamsRequest(resp));
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
        final int[] pAsArray = EndianConverterUtil.longToInts(pqPrimeFactors.first);
        final int[] qAsArray = EndianConverterUtil.longToInts(pqPrimeFactors.second);

        final MTProtoDataBuilder builder = new MTProtoDataBuilder();
        // method name
        builder.appendInt(0x83c95aec)
        // pq
        .appendIntArray(pq)
        // p
        .appendLong(pqPrimeFactors.first)
        // q
        .appendLong(pqPrimeFactors.second)
        // nonce
        .appendIntArray(prev128nonce)
        // server_nonce
        .appendIntArray(serverNonce)
        // new_nonce
        .appendIntArray(prev256nonce);

        return builder.build();
//        return new MTProtoDataHolder(new int[] {
//                // method name
//                0x83c95aec,
//                // pq
//                pq[0],
//                pq[1],
//                pq[2],
//                // p
//                pAsArray[0],
//                pAsArray[1],
//                // q
//                qAsArray[0],
//                qAsArray[1],
//                // nonce
//                prev128nonce[0],
//                prev128nonce[1],
//                prev128nonce[2],
//                prev128nonce[3],
//                // server_nonce
//                serverNonce[0],
//                serverNonce[1],
//                serverNonce[2],
//                serverNonce[3],
//                // new_nonce
//                prev256nonce[0],
//                prev256nonce[1],
//                prev256nonce[2],
//                prev256nonce[3],
//                prev256nonce[4],
//                prev256nonce[5],
//                prev256nonce[6],
//                prev256nonce[7]
//        });
    }

    /**
     * Evaluate prime factors for specified number
     * @param pqBytes
     * @return Prime factors as a pair of Long values
     */
    private Pair<Long, Long> getPQPrimeFactors(byte[] pqBytes) {
        byte[] pqMeaningBytes = Arrays.copyOfRange(pqBytes, 1, 1 + 8);
        Log.d(TAG, "Meaning PQ bytes are " + HexFormatterUtil.asString(pqMeaningBytes));
        long pq = EndianConverterUtil.bytesToLong(pqMeaningBytes);

        Log.d(TAG, "Evaluating prime factors for PQ=" + pq + " (" + HexFormatterUtil.asHexString(pq) + ")...");
        Pair<Long, Long> factors = MathUtil.primeFactors(pq);
        Log.d(TAG, "Prime factors for " + pq + " are " + factors.first + " x " + factors.second);

        return factors;
    }

    /**
     * Send req_DH_params#d712e4be command
     * @param prevResponse
     * @return Response
     */
    private MTProtoDataHolder generateDHParamsRequest(MTProtoDataHolder prevResponse) {
        Log.d(TAG, "Generating data in generateDHParamsRequest()");

        assert prevResponse != null;
        // TODO

        final MTProtoDataBuilder builder = new MTProtoDataBuilder();
        return builder.build();
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
