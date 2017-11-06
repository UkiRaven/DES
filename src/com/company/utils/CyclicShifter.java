package com.company.utils;

/**
 * Created by lastc on 27.10.2017.
 */

// ! Written only for 28 bits set, i.e. for C and D parts of the key

public class CyclicShifter {

    public static int shiftRight(int bits, int times) {
        for (int i = 0; i < times; i++) {
            bits = shiftRight(bits);
        }
        return bits;
    }

    public static int shiftLeft(int bits, int times) {
        for (int i = 0; i < times; i++) {
            bits = shiftLeft(bits);
        }
        return bits;
    }

    private static int shiftRight(int bits) {
        return (bits >> 1) | ((bits & 0x01) << 27);
    }

    private static int shiftLeft(int bits) {
        return (((bits << 1) & 0xFFFFFFF)) | ((bits & 0x8000000) >> 27);
    }
}