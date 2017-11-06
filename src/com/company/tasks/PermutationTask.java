package com.company.tasks;

import com.company.constants.PermutationTables;

import java.util.BitSet;
import java.util.concurrent.RecursiveAction;

/**
 * Created by lastc on 06.11.2017.
 */
public class PermutationTask extends RecursiveAction {

    private int threshold = 1000;
    private long[] array;
    private int start,end;
    private boolean isFinal;

    public PermutationTask(long[] array, int start, int end, boolean isFinal) {
        this.array = array;
        this.start = start;
        this.end = end;
        this.isFinal = isFinal;
    }

    @Override
    protected void compute() {
        if ((end - start) <= threshold) {
            for (int i = start; i < end; i++) {
                array[i] = isFinal? finPermutation(array[i]) : initPermutation(array[i]);
            }
        } else {
            int mid = (end + start) / 2;
            invokeAll(new PermutationTask(array, start, mid, isFinal), new PermutationTask(array, mid, end, isFinal));
        }
    }

    private long initPermutation(long block) {
        BitSet bitSet = BitSet.valueOf(new long[]{block});
        BitSet permuted = new BitSet(64);
        for (int j = 0, k = 63; j < permuted.size(); j++, k--) {
            permuted.set(k, bitSet.get(64 - PermutationTables.initPermutation[j]));
        }
        return permuted.toLongArray()[0];
    }

    private long finPermutation(long block) {
        block = block << 32 | block >>> 32;
        BitSet bitSet = BitSet.valueOf(new long[]{block});
        BitSet permuted = new BitSet(64);
        for (int j = 0, k = 63; j < permuted.size(); j++, k--) {
            permuted.set(k, bitSet.get(64-PermutationTables.finPermutation[j]));
        }
        return permuted.toLongArray()[0];
    }
}
