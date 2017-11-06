package com.company.tests;

import com.company.tasks.FeistelNetTask;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by lastc on 06.11.2017.
 */
public class FeistelNetTests {
    private Class<FeistelNetTask> clazz = FeistelNetTask.class;
    private FeistelNetTask task;
    private long[] data;
    private long roundKey = 0x36146478E1E1L;

    public FeistelNetTests() {
        data = new long[]{0xff8074fd00ff309aL};
        task = new FeistelNetTask(data, 0, data.length, roundKey);
    }
    @Test
    public void oneRoundTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        long[] res = new long[]{0x00ff309ae927e41eL};
        task.invoke();
        assertArrayEquals(res, data);
    }
    @Test
    public void sBoxTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        long block = 0x6117BA866527L;
        int nr = 0x5C82B597;
        BitSet set = sBox(BitSet.valueOf(new long[]{ block}));
        assertEquals(nr, set.toLongArray()[0]);

    }

    private BitSet sBox(BitSet bitSet) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getDeclaredMethod("sBox", BitSet.class);
        method.setAccessible(true);
        return (BitSet) method.invoke(task, bitSet);
    }
}
