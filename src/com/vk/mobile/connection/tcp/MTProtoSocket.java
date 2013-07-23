package com.vk.mobile.connection.tcp;

import android.util.Log;
import com.vk.mobile.util.EndianConverterUtil;
import com.vk.mobile.util.HexFormatterUtil;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by myltik
 * Created on 7/18/13 1:45 PM
 */
public class MTProtoSocket {

    private static final String TAG = MTProtoSocket.class.toString();

    private static final int SMALL_CHUNK_THRESHOLD = 0x7E;
    private static final int SMALL_CHUNK_BOUND_SYMBOL = 0x7F;

    protected final String host;
    protected final int port;

    private Socket sock = null;
    private OutputStream writer = null;
    private DataInputStream reader = null;

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
    public MTProtoDataHolder sendRequest(MTProtoDataHolder data) throws IOException {
        open(); // if needed

        // Send request
        byte[] preparedRequest = data.getBytesData();
        Log.d(TAG, "Writing request as array of bytes of length " + (preparedRequest.length)
                + ": [" + HexFormatterUtil.asString(preparedRequest) + "]");

        // Support for large data (more than 250 bytes), read http://dev.stel.com/mtproto#tcp-transport
        if (data.getPayloadLength() > SMALL_CHUNK_THRESHOLD) {
            writer.write(SMALL_CHUNK_BOUND_SYMBOL);
            writer.write(EndianConverterUtil.intToBytes(data.getPayloadLength(), 3));
        } else {
            writer.write(data.getPayloadLength());
        }

        writer.write(preparedRequest);
        writer.flush();

        // TODO Support for large data (more than 250 bytes), read http://dev.stel.com/mtproto#tcp-transport
        byte expectedSize = reader.readByte();
        if (expectedSize > SMALL_CHUNK_THRESHOLD) {
            Log.e(TAG, "NOT SUPPORTED: Response chunk size is more than " + SMALL_CHUNK_THRESHOLD + ". TODO support.");
        }
        int expectedSizeBytes = expectedSize*4;
        int offset = 0, res;
        byte[] buf = new byte[expectedSizeBytes]; // TODO Memory overflow o_O

        // Read response
        Log.d(TAG, "Expecting response of length " + expectedSize + " ints or " + expectedSizeBytes + " bytes");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (expectedSizeBytes > 0) {
            Arrays.fill(buf, (byte)0);
            res = reader.read(buf, offset, expectedSizeBytes);
            if (res == -1) {
                break;
            } else {
                expectedSizeBytes -= res;
            }

            Log.d(TAG, String.format("READ [0x%02x == %d] byte, expected=%d", (byte) res, (byte) res, expectedSizeBytes));
            bos.write(buf);
        }

        Log.d(TAG, "Initial response: [" + HexFormatterUtil.asString(bos.toByteArray())
                + "] of size " + bos.toByteArray().length + " bytes");

        return new MTProtoDataHolder(bos.toByteArray());
    }

    /**
     * Open socket and its writer and reader
     * @throws IOException
     */
    public void open() throws IOException {
        if (!isOpened()) {
            sock = new Socket(host, port);
            reader = new DataInputStream(sock.getInputStream());
            writer = new BufferedOutputStream(sock.getOutputStream());
//            reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            writer.write(0xEF); // TODO Only send before the first data chunk
            writer.flush();
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
