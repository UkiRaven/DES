package com.company;

import com.company.tasks.FeistelNetTask;
import com.company.tasks.PermutationTask;
import com.company.utils.ArrayUtils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ForkJoinPool;

public class DES {

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

    public void encrypt(Path filePath, long key) throws IOException {
        try {
            byte[] data = Files.readAllBytes(filePath);
            long before = System.currentTimeMillis();
            byte[] result = code(data, key, false);
            long after = System.currentTimeMillis();
            System.out.println("encryption time: " + (after-before) + "ms");
            Path encryptedFilePath = Paths.get("encrypted.txt");
            Files.deleteIfExists(encryptedFilePath);
            Files.createFile(encryptedFilePath);
            Files.write(encryptedFilePath, result);
            //Writing hex representation
            String res = ArrayUtils.byteArrayToHexString(result);
            String hexOut = "encrypted_hex.txt";
            BufferedWriter writer = new BufferedWriter( new FileWriter(hexOut));
            writer.write(res);
            writer.close();
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
            Path decryptedFilePath = Paths.get("decrypted.txt");
            Files.deleteIfExists(decryptedFilePath);
            Files.createFile(decryptedFilePath);
            Files.write(decryptedFilePath, result);
            //Writing hex representation
            String res = ArrayUtils.byteArrayToHexString(result);
            String hexOut = "decrypted_hex.txt";
            BufferedWriter writer = new BufferedWriter( new FileWriter(hexOut));
            writer.write(res);
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }

    public static void main(String[] args) throws IOException {
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
