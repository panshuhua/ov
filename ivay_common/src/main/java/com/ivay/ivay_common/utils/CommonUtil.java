package com.ivay.ivay_common.utils;

import java.math.BigDecimal;

public class CommonUtil {

    public static long longMultiplyBigDecimal(long a, BigDecimal... bigDecimal) {
        BigDecimal result = new BigDecimal(a);
        for (BigDecimal b : bigDecimal) {
            result = result.multiply(b);
        }

        return result.setScale(0, BigDecimal.ROUND_UP).longValue();
    }

    public static long longAddBigDecimal(long a, BigDecimal... bigDecimal) {
        BigDecimal result = new BigDecimal(a);
        for (BigDecimal b : bigDecimal) {
            result = result.add(b);
        }
        return result.setScale(0, BigDecimal.ROUND_UP).longValue();
    }

    public static long longSubtractBigDecimal(long a, BigDecimal... bigDecimal) {
        BigDecimal result = new BigDecimal(a);
        for (BigDecimal b : bigDecimal) {
            result = result.subtract(b);
        }
        return result.setScale(0, BigDecimal.ROUND_UP).longValue();
    }

    public static void main(String[] args) {
        System.out.println(longMultiplyBigDecimal(10000, new BigDecimal(0.002)));
        System.out.println(longAddBigDecimal(10000, new BigDecimal[]{new BigDecimal(999), new BigDecimal(1001)}));
    }
}
