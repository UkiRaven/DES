package com.company.tests;

import com.company.tasks.PermutationTask;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/**
 * Created by lastc on 05.11.2017.
 */

public class PermutationTests {

    @Test
    public void initPermutationTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        long[] array = new long[] {0x123456789ABCDEFL};
        new PermutationTask(array, 0, 1, false).invoke();
        assertEquals(0xCC00CCFFF0AAF0AAL, array[0]);
    }

    @Test
    public void finPermutationTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        long[] array = new long[] {0x434232340A4CD995L};
        new PermutationTask(array, 0, 1, true).invoke();
        assertEquals(0x85E813540F0AB405L, array[0]);
    }

}
