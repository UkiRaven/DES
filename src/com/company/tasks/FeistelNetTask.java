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
        }
        else {
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
        BitSet[] vectors = new BitSet[8];
        for (int i = 7, k = 0; i >=0; i--) {
            vectors[i] = new BitSet(6);
            for (int j = 0; j < 6; j++, k++) {
                vectors[i].set(j, bitSet.get(k));
            }
        }
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 4;
            BitSet set = vectors[i];
            int m = Integer.parseInt((set.get(5)? "1" : "0")  + (set.get(0) ? "1" : "0"), 2);
            int l =  Integer.parseInt((set.get(4)? "1" : "0")  + (set.get(3) ? "1" : "0") +
                    (set.get(2)? "1" : "0") + (set.get(1)? "1" : "0"), 2 );
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
