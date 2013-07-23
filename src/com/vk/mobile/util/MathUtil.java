package com.vk.mobile.util;

import android.util.Pair;

/**
 * Created by myltik
 * Created on 7/23/13 3:48 PM
 */
public class MathUtil {

    /**
     * @param origNumber
     * @return Pair of factors, in resulting pair First value should be less than Second value
     */
    public static Pair<Long, Long> primeFactors(final long origNumber) {
        long first = (long) Math.sqrt(origNumber);
        if (first % 2 == 0) ++first;

        for (; first > 2; first-=2) {
            if (origNumber % first == 0) {
                break;
            }
        }

        long second = origNumber/first;

        if (first > second) {
            return new Pair<Long, Long>(Long.valueOf(second), Long.valueOf(first));
        } else {
            return new Pair<Long, Long>(Long.valueOf(first), Long.valueOf(second));
        }
    }

    /**
     * @param origNumber
     * @return Pair of factors, in resulting pair First value should be less than Second value
     */
    public static Pair<Long, Long> primeFactorsGoodButSlow(final long origNumber) {
        long first;
        long number = origNumber;

        for (first = 2; first <= number; ++first) {
            if (number % first == 0) {
                number /= first;
                --first;
            }
        }

        long second = origNumber/first;

        if (first > second) {
            return new Pair<Long, Long>(Long.valueOf(second), Long.valueOf(first));
        } else {
            return new Pair<Long, Long>(Long.valueOf(first), Long.valueOf(second));
        }
    }
}
