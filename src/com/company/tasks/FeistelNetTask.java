package com.company.tasks;

import com.company.constants.PermutationTables;

import java.util.BitSet;
import java.util.concurrent.RecursiveAction;

/**
 * Created by lastc on 06.11.2017.
 */
public class FeistelNetTask extends RecursiveAction {

    private long[] array;
    private long key;
    private final int threshold = 1000;
    private int start, end;

    public FeistelNetTask(long[] array, int start, int end, long key) {
        this.start = start;
        this.end = end;
        this.array = array;
        this.key = key;
    }

    @Override
    protected void compute() {
        if ((end - start) <= threshold) {
            for (int i = start; i < end; i++) {
                long left = array[i] >>> 32;
                long right = (array[i] << 32) >>> 32;
                BitSet bitSet = extendBlock(right);
                BitSet keySet = BitSet.valueOf(new long[]{key});
                bitSet.xor(keySet);
                bitSet = sBox(bitSet);
                bitSet = pBox(bitSet);
                array[i] = right << 32 | bitSet.toLongArray()[0] ^ left;
            }
        } else {
            int middle = (end + start) / 2;
            invokeAll(new FeistelNetTask(array, start, middle, key), new FeistelNetTask(array, middle, end, key));
        }
    }

    private BitSet extendBlock(long block) {
        BitSet bitSet = BitSet.valueOf(new long[]{block});
        BitSet extended = new BitSet(48);
        for (int i = 0, k = 47; i < 48; i++, k--) {
            extended.set(k, bitSet.get(32- PermutationTables.extend[i]));
        }
        return extended;
    }

    private BitSet sBox(BitSet bitSet) {
        long result = 0;
        for (int i= 0, k = 47; i < 8 ; i++, k -= 6) {
            result <<= 4;
            int m = (bitSet.get(k - 5)? 0b1 : 0) | (bitSet.get(k) ? 0b10 : 0);
            int l = (bitSet.get(k - 4)? 0b1 : 0)  | (bitSet.get(k - 3) ? 0b10 : 0) +
                    (bitSet.get(k - 2)? 0b100 : 0) + (bitSet.get(k - 1)? 0b1000 : 0);
            int num = PermutationTables.sBoxes[i][l][m];
            result |= num;
        }
        return BitSet.valueOf(new long[]{result});
    }

    private BitSet pBox(BitSet bitSet) {
        BitSet result = new BitSet(32);
        for (int i = 0, k = 31; i < 32; i++, k--) {
            result.set(k, bitSet.get(32 - PermutationTables.pboxes[i]));
        }
        return result;
    }
}
