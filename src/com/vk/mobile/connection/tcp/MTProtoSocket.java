package com.vk.mobile.connection.tcp;

import android.util.Log;
import com.vk.mobile.util.EndianConverterUtil;
import com.vk.mobile.util.HexFormatterUtil;

import java.io.*;
import java.net.Socket;

/**
 * Created by myltik
 * Created on 7/18/13 1:45 PM
 */
public class MTProtoSocket {

    private static final String TAG = MTProtoSocket.class.toString();

    protected final String host;
    protected final int port;

    private Socket sock = null;
    private OutputStream writer = null;
    private Reader reader = null;

    /**
     * @param host
     * @param port
     */
    public MTProtoSocket(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Send request to the server
     * @param data
     * @return
     * @throws IOException
     */
    public int[] sendRequest(int[] data) throws IOException {
        open(); // if needed

        // Send request
        byte[] preparedRequest = EndianConverterUtil.intToByteArray(data);
        Log.d(TAG, "Writing request as array of bytes of length " + (preparedRequest.length)
                + ": [" + HexFormatterUtil.asString(preparedRequest) + "]");
        writer.write(0xEF); // TODO Only send before the first data chunk
        writer.write(data.length/4); // TODO Support for large data (more than 250 bytes), read http://dev.stel.com/mtproto#tcp-transport
        writer.write(preparedRequest);
        writer.flush();

        // Read response
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int val;
        while ((val = reader.read()) != -1) {
            Log.d(TAG, "READ [" + val + "] byte");
            bos.write(val);
        }

        Log.d(TAG, "Initial response: [" + HexFormatterUtil.asString(bos.toByteArray()) + "]");

        return EndianConverterUtil.byteToIntArray(bos.toByteArray());
    }

    /**
     * Open socket and its writer and reader
     * @throws IOException
     */
    public void open() throws IOException {
        if (!isOpened()) {
            sock = new Socket(host, port);
            writer = sock.getOutputStream();
            reader = new InputStreamReader(sock.getInputStream());
            //writer = new BufferedOutputStream(sock.getOutputStream());
            //reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        }
    }

    /**
     * Close socket and its writer and reader
     */
    public void close() {
        if (writer != null) {
            try {
                writer.close();
            }
            catch (Throwable t) {
                Log.e(TAG, "Cannot gracefully close writer: " + t.getMessage(), t);
            }
        }

        if (reader != null) {
            try {
                reader.close();
            } catch (Throwable t) {
                Log.e(TAG, "Cannot gracefully close reader: " + t.getMessage(), t);
            }
        }

        if (sock != null) {
            try {
                sock.close();
            } catch (Throwable t) {
                Log.e(TAG, "Cannot gracefully close socket: " + t.getMessage(), t);
            }
        }

        sock = null;
        writer = null;
        reader = null;
    }

    /**
     * @return true is connection is opened, false otherwise
     */
    public boolean isOpened() {
        return sock != null && writer != null && reader != null && !sock.isClosed();
    }

    @Override
    public String toString() {
        return "MTProtoSocket{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", isOpened=" + isOpened() +
                '}';
    }
}
