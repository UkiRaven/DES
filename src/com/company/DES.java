package com.company;

import com.company.tasks.FeistelNetTask;
import com.company.tasks.PermutationTask;
import com.company.utils.ArrayUtils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ForkJoinPool;

public class DES {
    private static final Path ENCRYPTED_PATH = Paths.get("encrypted.txt");
    private static final Path ENCRYPTED_HEX_PATH = Paths.get("encrypted_hex.txt");
    private static final Path DECRYPTED_PATH = Paths.get("decrypted.txt");
    private static final Path DECRYPTED_HEX_PATH = Paths.get("decrypted_hex.txt");

    private ForkJoinPool fjp = ForkJoinPool.commonPool();

    private void initPermutation(long[] blocks) {
        fjp.invoke(new PermutationTask(blocks, 0, blocks.length, false));
    }

    private void finPermutation(long[] blocks) {
        fjp.invoke(new PermutationTask(blocks, 0, blocks.length, true));
    }

    private void oneRound(long[] array, long key) {
        fjp.invoke(new FeistelNetTask(array,0, array.length, key));
    }

    private byte[] code(byte[] byteDate, long key, boolean decode) {
        byteDate = ArrayUtils.extendSize(byteDate);
        long[] blocks = ArrayUtils.separateBitsToLong(byteDate);
        initPermutation(blocks);
        RoundKeyGenerator keyGenerator = new RoundKeyGenerator(key);
        for (int i = 0; i < 16; i++) {
            long roundKey = keyGenerator.getRoundKey(i, decode);
            oneRound(blocks, roundKey);
        }
        finPermutation(blocks);
        return ArrayUtils.uniteBits(blocks);
    }

    private void writeResult(byte[] result, Path path) throws IOException {
        Files.deleteIfExists(path);
        Files.createFile(path);
        Files.write(path, result);
    }
    private void writeHexResult(byte[] result, Path path) throws IOException {
        String res = ArrayUtils.byteArrayToHexString(result);
        String hexOut = path.toString();
        BufferedWriter writer = new BufferedWriter(new FileWriter(hexOut));
        writer.write(res);
        writer.close();
    }

    public void encrypt(Path filePath, long key) throws IOException {
        try {
            byte[] data = Files.readAllBytes(filePath);
            long before = System.currentTimeMillis();
            byte[] result = code(data, key, false);
            long after = System.currentTimeMillis();
            System.out.println("encryption time: " + (after-before) + "ms");
            writeResult(result, ENCRYPTED_PATH);
            writeHexResult(result, ENCRYPTED_HEX_PATH);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }

    public void decrypt(Path filePath, long key) throws IOException {
        try {
            byte[] data = Files.readAllBytes(filePath);
            long before = System.currentTimeMillis();
            byte[] result = code(data, key, true);
            long after = System.currentTimeMillis();
            System.out.println("decryption time: " + (after-before) + "ms");
            writeResult(result, DECRYPTED_PATH);
            writeHexResult(result, DECRYPTED_HEX_PATH);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }

    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (args.length == 3) {
            String mode = args[0];
            Path path = Paths.get(args[1]);
            long key = Long.parseLong(args[2], 16);
            DES des = new DES();
            if (mode.toLowerCase().equals("encrypt")) {
                des.encrypt(path, key);
            } else if (mode.toLowerCase().equals("decrypt")) {
                des.decrypt(path, key);
            } else {
                System.out.println("Wrong mode");
            }
        } else {
            System.out.println("Invalid arguments");
        }
    }


}
