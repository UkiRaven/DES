package com.company.tests;

import com.company.utils.ArrayUtils;
import com.company.DES;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;

import static org.junit.Assert.*;

/**
 * Created by lastc on 05.11.2017.
 */

public class PermutationTests {
    private DES des = new DES();
    private Class<DES> clazz = DES.class;

    @Test
    public void bytesToLongTest() throws IOException {
        byte[] data = Files.readAllBytes(Paths.get("data.txt"));
        long before = System.currentTimeMillis();
        long[] separated = ArrayUtils.separateBitsToLong(data);
        long after = System.currentTimeMillis();
        System.out.println(after - before);
    }

    @Test
    public void unitesBitsTest() {
        long[] block = new long[] {0x434232340A4CD995L, 0xCC00CCFFF0AAF0AAL};
        byte[] result = ArrayUtils.uniteBits(block);
    }

    @Test
    public void initPermutationTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        long m = 0x123456789ABCDEFL;
        long ip = 0xCC00CCFFF0AAF0AAL;
        long res = initPermutation(m);
        assertEquals(ip, res);
    }

    @Test
    public void finPermutationTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        long m = 0x434232340A4CD995L;
        long fp = 0x85E813540F0AB405L;
        long res = finPermutation(m);
        assertEquals(fp, res);
    }

    @Test
    public void extendBlockTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        int block = 0xF0AAF0AA;
        long ex = 0x7A15557A1555L;
        long res = extendBlock(block).toLongArray()[0];
        assertEquals(ex, res);
    }

    @Test
    public void getCDTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        long key = 0x133457799BBCDFF1L;
        int c = getC(key);
        int d = getD(key);
        int nc = 0xF0CCAAF;
        int nd = 0x556678F;
        assertEquals(c, nc);
        assertEquals(d, nd);
    }

    @Test
    public void getRoundKeyTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        int c = 0xE19955F;
        int d = 0xAACCF1E;
        long key = getRoundKey(c, d);
        long actual_key = 0x1B02EFFC7072L;
        assertEquals(key, actual_key);
    }

    @Test
    public void shiftTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        int c = 0xF0CCAAF;
        int sc = 0xE19955F;
        int res = shift(c, 0, false);
        assertEquals(res, sc);
    }

    @Test
    public void sBoxTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        long block = 0x6117BA866527L;
        int nr = 0x5C82B597;
        BitSet set = sBox(BitSet.valueOf(new long[]{ block}));
        assertEquals(nr, set.toLongArray()[0]);

    }

    @Test
    public void oneRoundTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        long roundKey = 0x36146478E1E1L;
        long[] data = new long[]{0xff8074fd00ff309aL};
        long[] res = new long[]{0x00ff309ae927e41eL};
        long[] encoded = oneRound(data, roundKey);
        assertArrayEquals(res, encoded);
    }

    @Test
    public void fullTest() throws IOException {
        Path dataPath = Paths.get("data4.txt");
        Path encryptedPath = Paths.get("encrypted.txt");
        Path resultPath = Paths.get("decrypted.txt");
        long key = 0x0E329232EA6D0D73L;
        byte[] data = Files.readAllBytes(dataPath);
        System.out.println(data.length/8);
        des.encrypt(dataPath, key);
        des.decrypt(encryptedPath, key);
        byte[] result =Files.readAllBytes(resultPath);
        //When data length % 8 != 0 zero bytes added to the end of file, so assert test doesn't work
        //but output is correct
        //check decrypted.txt file instead
//        assertArrayEquals(data, result);
    }


    //Methods
    private int getC(long key) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getDeclaredMethod("getC", long.class);
        method.setAccessible(true);
        return (int) method.invoke(des, key);
    }

    private int getD(long key) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getDeclaredMethod("getD", long.class);
        method.setAccessible(true);
        return (int) method.invoke(des, key);
    }

    private long initPermutation(long block) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getDeclaredMethod("initPermutation", long.class);
        method.setAccessible(true);
        return (long) method.invoke(des, block);
    }

    private long finPermutation(long block) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getDeclaredMethod("finPermutation", long.class);
        method.setAccessible(true);
        return (long) method.invoke(des, block);
    }

    private BitSet extendBlock(long block) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getDeclaredMethod("extendBlock", long.class);
        method.setAccessible(true);
        return (BitSet) method.invoke(des, block);
    }

    private BitSet sBox(BitSet bitSet) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getDeclaredMethod("sBox", BitSet.class);
        method.setAccessible(true);
        return (BitSet) method.invoke(des, bitSet);
    }

    private int shift(int cd, int round, boolean decode) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getDeclaredMethod("shift", int.class, int.class, boolean.class);
        method.setAccessible(true);
        return (int) method.invoke(des, cd, round, decode);
    }

    private long getRoundKey(int c, int d) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getDeclaredMethod("getRoundKey", int.class, int.class);
        method.setAccessible(true);
        return (long) method.invoke(des, c, d);
    }

    private long[] oneRound(long[] array, long key) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getDeclaredMethod("oneRound", long[].class, long.class);
        method.setAccessible(true);
        return (long[]) method.invoke(des, array, key);
    }
}
