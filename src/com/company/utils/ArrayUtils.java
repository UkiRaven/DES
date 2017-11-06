package com.company.utils;

/**
 * Created by lastc on 30.10.2017.
 */
public class ArrayUtils {

    public static byte[] extendSize(byte[] array)  {
        int i = array.length;
        if (array.length % 8 != 0) {
            while (i % 8 != 0) i++;
        } else {
            return array;
        }
        byte[] extendedArray = new byte[i];
        System.arraycopy(array, 0, extendedArray, 0, array.length);
        return extendedArray;
    }

    public static long[] separateBitsToLong(byte[] array) {
        long[] separated = new long[array.length/Byte.SIZE];
        for (int i = 0, k = 0; i < array.length/Byte.SIZE; i++) {
            long result = 0;
            for (int j = 0; j < 8; j++, k++) {
                result <<= 8;
                result |= (array[k] & 0xFF);
            }
            separated[i] = result;
        }
        return separated;
    }

    public static byte[] uniteBits(long[] array) {
        byte[] result = new byte[array.length * Byte.SIZE];
        for (int i = array.length-1, k = result.length-1; i >= 0; i--) {
            for (int j = 0; j < 8 ; j++, k--) {
                result[k] = (byte)(array[i] & 0xFF);
                array[i] >>= 8;
            }
        }
        return result;
    }

    public static String byteArrayToHexString(byte[] array) {
        StringBuilder builder = new StringBuilder();
        for (byte anArray : array) {
            builder.append(String.format("%02x", anArray));
        }
        return builder.toString();
    }

}
