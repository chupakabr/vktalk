package com.vk.mobile.connection.tcp;

import android.test.AndroidTestCase;

import java.util.Arrays;

/**
 * Created by myltik
 * Created on 7/24/13 1:58 PM
 */
public class MTProtoDataBuilderTest extends AndroidTestCase {

    public void testBuildLargeData() {
        final int bufSize = 140;
        final byte[] chunk1 = new byte[bufSize];
        final byte[] chunk2 = new byte[bufSize];

        Arrays.fill(chunk1, (byte)0x11);
        Arrays.fill(chunk2, (byte)0x22);

        final MTProtoDataBuilder builder = new MTProtoDataBuilder();
        builder.appendByteArray(chunk1);
        builder.appendByteArray(chunk2);

        MTProtoDataHolder dataHolder = builder.build();
        assertNotNull(dataHolder);
        assertEquals(bufSize*2/4, dataHolder.getPayloadLength());

        final byte[] res = dataHolder.getBytesData();
        assertEquals(bufSize*2, res.length);

        final byte[] resChunk1 = dataHolder.getBytes(0, bufSize);
        final byte[] resChunk2 = dataHolder.getBytes(bufSize, bufSize);
        assertTrue(Arrays.equals(chunk1, resChunk1));
        assertTrue(Arrays.equals(chunk2, resChunk2));
    }

}
