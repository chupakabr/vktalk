package com.vk.mobile.connection.tcp;

import android.os.AsyncTask;
import android.util.Log;
import com.vk.mobile.util.HexFormatterUtil;
import com.vk.mobile.util.TimeUtil;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by myltik
 * Created on 7/19/13 1:17 PM
 */
public class AuthAsyncTask extends AsyncTask<MTProtoServerInfo, Integer, MTProtoDataHolder> {

    private static final String TAG = AuthAsyncTask.class.toString();

    @Override
    protected MTProtoDataHolder doInBackground(MTProtoServerInfo... params) {
        Log.d(TAG, "Starting Authentication...");

        int[] resp = doAuth(params[0]);

        Log.d(TAG, "FINAL RESPONSE: [" + HexFormatterUtil.asString(resp) + "]");
        Log.d(TAG, "Finish Authentication");

        return new MTProtoDataHolder(resp);
    }

    /**
     * Authenticate to the server via MTProto
     * @param serverInfo
     * @return Last response
     */
    private int[] doAuth(MTProtoServerInfo serverInfo) {
        int[] resp = null;
        MTProtoSocket sock = null;

        try {
            sock = new MTProtoSocket(serverInfo.getHost(), serverInfo.getPort());

            resp = sendRequest("111 req_pq#60469778", sock, generatePQRequest());
//            resp = sendRequest("222 p_q_inner_data#83c95aec", sock, generatePQInnerDataRequest(resp));
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
    private int[] sendRequest(final String flashTag, final MTProtoSocket sock, final int[] request) throws IOException {
        Log.d(TAG, flashTag + " Request of length " + (request.length*4) + ": [" + HexFormatterUtil.asString(request) + "]");

        int[] resp = sock.sendRequest(request);
        Log.d(TAG, flashTag + " Response of length " + (resp.length*4) + " : [" + HexFormatterUtil.asString(resp) + "]");

        return resp;
    }

    /**
     * Send req_pq#60469778 command
     * @return Response
     */
    private int[] generatePQRequest() {
        Random rnd = new Random(System.currentTimeMillis());
        final int[] random128bit = new int[] {rnd.nextInt(), rnd.nextInt(), rnd.nextInt(), rnd.nextInt()};

        return new int[] {
                // auth_key_id
                0x00000000,
                0x00000000,
                // message_id
                0x00000000,
                TimeUtil.currentUnixtime(),
                // message_length
                0x20,
                // req_pq call
                0x60469778,
                // nonce
                random128bit[0],
                random128bit[1],
                random128bit[2],
                random128bit[3]
        };
    }

    /**
     * Send p_q_inner_data#83c95aec command
     * @param prevResponse
     * @return Response
     */
    private int[] generatePQInnerDataRequest(int[] prevResponse) {
        // TODO

        return new int[] {

        };
    }

    /**
     * Send req_DH_params#d712e4be command
     * @param prevResponse
     * @return Response
     */
    private int[] generateDHParamsRequest(int[] prevResponse) {
        // TODO

        return new int[] {

        };
    }

    /**
     * Send server_DH_inner_data#b5890dba command
     * @param prevResponse
     * @return Response
     */
    private int[] generateDHInnerDataRequest(int[] prevResponse) {
        // TODO

        return new int[] {

        };
    }

    /**
     * Send client_DH_inner_data#6643b654 command
     * @param prevResponse
     * @return Response
     */
    private int[] generateDHClientInnerDataRequest(int[] prevResponse) {
        // TODO

        return new int[] {

        };
    }

    /**
     * Send set_client_DH_params#f5045f1f command
     * @param prevResponse
     * @return Response
     */
    private int[] generateDHClientSetParamsRequest(int[] prevResponse) {
        // TODO

        return new int[] {

        };
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
}
