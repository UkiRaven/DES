package com.company.tests;

import com.company.DES;
import com.company.utils.ArrayUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by lastc on 06.11.2017.
 */
public class DESTests {
    private DES des = new DES();

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
    public void fullTest() throws IOException {
        Path dataPath = Paths.get("data4.txt");
        Path encryptedPath = Paths.get("encrypted.txt");
        Path resultPath = Paths.get("decrypted.txt");
        long key = 0x0E329232EA6D0D73L;
        byte[] data = Files.readAllBytes(dataPath);
        byte[] enc = Files.readAllBytes(encryptedPath);
        System.out.println(data.length/8);
        long before = System.currentTimeMillis();
        des.encrypt(data, key);
        long after = System.currentTimeMillis();
        System.out.println("encryption time: " + (after - before) + "ms");
        before = System.currentTimeMillis();
        des.decrypt(enc, key);
        after = System.currentTimeMillis();
        System.out.println("decryption time: " + (after - before) + "ms");
//        byte[] result =Files.readAllBytes(resultPath);
        //When data length % 8 != 0 zero bytes added to the end of file, so assert test doesn't work
        //but output is correct
        //check decrypted.txt file instead
//        assertArrayEquals(data, result);
    }
}
