package com.vk.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.vk.mobile.connection.tcp.AuthAsyncTask;
import com.vk.mobile.connection.tcp.MTProtoServerInfo;
import com.vk.mobile.util.TimeUtil;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.toString();

    private static final String SERVER_PUBLIC_RSA_KEY = "-----BEGIN RSA PUBLIC KEY-----\n" +
            "MIIBCgKCAQEAwVACPi9w23mF3tBkdZz+zwrzKOaaQdr01vAbU4E1pvkfj4sqDsm6\n" +
            "lyDONS789sVoD/xCS9Y0hkkC3gtL1tSfTlgCMOOul9lcixlEKzwKENj1Yz/s7daS\n" +
            "an9tqw3bfUV/nqgbhGX81v/+7RFAEd+RwFnK7a+XYl9sluzHRyVVaTTveB2GazTw\n" +
            "Efzk2DWgkBluml8OREmvfraX3bkHZJTKX4EQSjBbbdJ2ZXIsRrYOXfaA+xayEGB+\n" +
            "8hdlLmAjbCVfaigxX0CDqWeR1yFL9kwd9P0NsZRPsmoqVwMbMu7mStFai6aIhc3n\n" +
            "Slv8kg9qv1m6XHVQY3PnEw+QQtqSIXklHwIDAQAB\n" +
            "-----END RSA PUBLIC KEY-----";

    private static final String SERVER_HOST = "95.142.192.65";
    private static final int SERVER_PORT = 80;
    private static final String SERVER_URL = SERVER_HOST + ":" + SERVER_PORT;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onRegisterClick(View view) {
        Log.d(TAG, "Start registration activity on " + SERVER_URL + ", current unixtime is " + TimeUtil.currentUnixtime());

        try {
            new AuthAsyncTask().execute(new MTProtoServerInfo(SERVER_HOST, SERVER_PORT, SERVER_PUBLIC_RSA_KEY));
        } catch (Throwable t) {
            Log.e(TAG, "Cannot register: " + t.getMessage(), t);
        }

        Log.d(TAG, "Finish starting registration activity on " + SERVER_URL);
    }
}
