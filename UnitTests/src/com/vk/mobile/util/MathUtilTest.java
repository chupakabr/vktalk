package com.vk.mobile.util;

import android.test.AndroidTestCase;
import android.util.Log;
import android.util.Pair;

/**
 * Created by myltik
 * Created on 7/23/13 4:43 PM
 */
public class MathUtilTest extends AndroidTestCase {

    private static final String TAG = MathUtilTest.class.toString();

    public void testPrimeFactors1() {
        long val = 100;
        Pair<Long, Long> pair = MathUtil.primeFactors(val);

        Log.i(TAG, "Prime factors for " + val + " are " + pair.first + " x " + pair.second);

        assertEquals(5, pair.first.longValue());
        assertEquals(20, pair.second.longValue());
    }

    public void testPrimeFactors2() {
        long val = 13195;
        Pair<Long, Long> pair = MathUtil.primeFactors(val);

        Log.i(TAG, "Prime factors for " + val + " are " + pair.first + " x " + pair.second);

        assertEquals(91, pair.first.longValue());
        assertEquals(145, pair.second.longValue());
    }

    public void testPrimeFactors3() {
        long val = 2649369530408890403l;
        Pair<Long, Long> pair = MathUtil.primeFactors(val);

        Log.i(TAG, "Prime factors for " + val + " are " + pair.first + " x " + pair.second);

        assertEquals(1409623049l, pair.first.longValue());
        assertEquals(1879487947l, pair.second.longValue());
    }

    public void testPrimeFactors4() {
        long val = 1724114033281923457l;
        Pair<Long, Long> pair = MathUtil.primeFactors(val);

        Log.i(TAG, "Prime factors for " + val + " are " + pair.first + " x " + pair.second);

        assertEquals(1229739323l, pair.first.longValue());
        assertEquals(1402015859l, pair.second.longValue());
    }

    public void testPrimeFactors5() {
        long val = 3431210488999628191l;
        Pair<Long, Long> pair = MathUtil.primeFactors(val);

        Log.i(TAG, "Prime factors for " + val + " are " + pair.first + " x " + pair.second);

        assertEquals(1805061131l, pair.first.longValue());
        assertEquals(1900883261l, pair.second.longValue());
    }
}
