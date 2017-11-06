package com.company;

import com.company.constants.PermutationTables;
import com.company.utils.CyclicShifter;

import java.util.BitSet;

/**
 * Created by lastc on 06.11.2017.
 */
public class RoundKeyGenerator {
    private int c;
    private int d;

    public int getC() {
        return c;
    }

    public int getD() {
        return d;
    }

    public RoundKeyGenerator(long key) {
        this.c = computeC(key);
        this.d = computeD(key);
    }

    private int computeC(long key) {
        BitSet bitSet = BitSet.valueOf(new long[]{key});
        BitSet c = new BitSet(28);
        for (int i = 0, k = 27; i < 28 ; i++, k--) {
            c.set(k, bitSet.get(64 - PermutationTables.keyPermutation[i]));
        }
        return (int) c.toLongArray()[0];
    }

    private int computeD(long key) {
        BitSet bitSet = BitSet.valueOf(new long[]{key});
        BitSet d = new BitSet(28);
        for (int i = 28, k = 27; i < 56 ; i++, k--) {
            d.set(k, bitSet.get(64-PermutationTables.keyPermutation[i]));
        }
        return (int) d.toLongArray()[0];
    }

    private int shift(int cd, int round, boolean decode) {
        if (!decode) {
            cd = CyclicShifter.shiftLeft(cd, PermutationTables.roundShift[round]);
        }
        else {
            cd = CyclicShifter.shiftRight(cd, PermutationTables.roundShiftInverse[round]);
        }
        return cd;
    }

    private long narrowKey(long key) {
        BitSet keySet = BitSet.valueOf(new long[] {key});
        BitSet narrowed = new BitSet(48);
        for (int i = 0, k = 47; i < 48; i++, k--) {
            narrowed.set(k, keySet.get(56-PermutationTables.narrowKey[i]));
        }
        return narrowed.toLongArray()[0];
    }

    public long getRoundKey(int round, boolean decode) {
        c = shift(c, round, decode);
        d = shift(d, round, decode);
        return narrowKey(((long)c << 28) | d);
    }

}
