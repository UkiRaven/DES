package com.company.tests;

import com.company.RoundKeyGenerator;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;
/**
 * Created by lastc on 06.11.2017.
 */
public class RoundKeyGeneratorTests {
    private RoundKeyGenerator generator = new RoundKeyGenerator(0x133457799BBCDFF1L);
    private Class<RoundKeyGenerator> clazz = RoundKeyGenerator.class;

    @Test
    public void getRoundKeyTest() {
        long key = generator.getRoundKey(0, false);
        long actual_key = 0x1B02EFFC7072L;
        assertEquals(key, actual_key);
    }

    @Test
    public void getCDTest() {
        int c = generator.getC();
        int d = generator.getD();
        int nc = 0xF0CCAAF;
        int nd = 0x556678F;
        assertEquals(c, nc);
        assertEquals(d, nd);
    }

    @Test
    public void shiftTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        int c = 0xF0CCAAF;
        int sc = 0xE19955F;
        int res = shift(c, 0, false);
        assertEquals(res, sc);
    }

    private int shift(int cd, int round, boolean decode) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getDeclaredMethod("shift", int.class, int.class, boolean.class);
        method.setAccessible(true);
        return (int) method.invoke(generator, cd, round, decode);
    }


}
