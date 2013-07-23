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
        Pair<Integer, Integer> pair = MathUtil.primeFactors(val);

        Log.i(TAG, "Prime factors for " + val + " are " + pair.first + " x " + pair.second);

        assertEquals(5, pair.first.intValue());
        assertEquals(20, pair.second.intValue());
    }

    public void testPrimeFactors2() {
        long val = 13195;
        Pair<Integer, Integer> pair = MathUtil.primeFactors(val);

        Log.i(TAG, "Prime factors for " + val + " are " + pair.first + " x " + pair.second);

        assertEquals(91, pair.first.intValue());
        assertEquals(145, pair.second.intValue());
    }

    public void testPrimeFactors3() {
        long val = 2649369530408890403l;
        Pair<Integer, Integer> pair = MathUtil.primeFactors(val);

        Log.i(TAG, "Prime factors for " + val + " are " + pair.first + " x " + pair.second);

        assertEquals(1409623049, pair.first.intValue());
        assertEquals(1879487947, pair.second.intValue());
    }

    public void testPrimeFactors4() {
        long val = 1724114033281923457l;
        Pair<Integer, Integer> pair = MathUtil.primeFactors(val);

        Log.i(TAG, "Prime factors for " + val + " are " + pair.first + " x " + pair.second);

        assertEquals(1229739323, pair.first.intValue());
        assertEquals(1402015859, pair.second.intValue());
    }

    public void testPrimeFactors5() {
        long val = 3431210488999628191l;
        Pair<Integer, Integer> pair = MathUtil.primeFactors(val);

        Log.i(TAG, "Prime factors for " + val + " are " + pair.first + " x " + pair.second);

        assertEquals(1805061131, pair.first.intValue());
        assertEquals(1900883261, pair.second.intValue());
    }
}
