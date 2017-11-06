package com.company;

import com.company.constants.PermutationTables;
import com.company.utils.ArrayUtils;
import com.company.utils.CyclicShifter;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;

public class DES {

    private long initPermutation(long block) {
        BitSet bitSet = BitSet.valueOf(new long[]{block});
        BitSet permuted = new BitSet(64);
        for (int i = 0, k = 63; i < permuted.size(); i++, k--) {
            permuted.set(k, bitSet.get(64 - PermutationTables.initPermutation[i]));
        }
        return permuted.toLongArray()[0];
    }

    private long finPermutation(long block) {
        block = block << 32 | block >>> 32;
        BitSet bitSet = BitSet.valueOf(new long[]{block});
        BitSet permuted = new BitSet(64);
        for (int i = 0, k = 63; i < permuted.size(); i++, k--) {
            permuted.set(k, bitSet.get(64-PermutationTables.finPermutation[i]));
        }
        return permuted.toLongArray()[0];
    }

    private BitSet extendBlock(long block) {
        BitSet bitSet = BitSet.valueOf(new long[]{block});
        BitSet extended = new BitSet(48);
        for (int i = 0, k = 47; i < 48; i++, k--) {
            extended.set(k, bitSet.get(32-PermutationTables.extend[i]));
        }
        return extended;
    }

    private BitSet sBox(BitSet bitSet) {
        //divide by 8 vectors
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

    private int getC(long key) {
        BitSet bitSet = BitSet.valueOf(new long[]{key});
        BitSet c = new BitSet(28);
        for (int i = 0, k = 27; i < 28 ; i++, k--) {
            c.set(k, bitSet.get(64 - PermutationTables.keyPermutation[i]));
        }
        return (int) c.toLongArray()[0];
    }

    private int getD(long key) {
        BitSet bitSet = BitSet.valueOf(new long[]{key});
        BitSet d = new BitSet(28);
        for (int i = 28, k = 27; i < 56 ; i++, k--) {
            d.set(k, bitSet.get(64-PermutationTables.keyPermutation[i]));
        }
        return (int) d.toLongArray()[0];
    }

    private int shift(int cd, int round, boolean decode) {
        if (!decode) {
            cd = CyclicShifter.shiftLeft(cd, PermutationTables.roundShift[round]);
        }
        else {
            cd = CyclicShifter.shiftRight(cd, PermutationTables.roundShiftInverse[round]);
        }
        return cd;
    }

    private long narrowKey(long key) {
        BitSet keySet = BitSet.valueOf(new long[] {key});
        BitSet narrowed = new BitSet(48);
        for (int i = 0, k = 47; i < 48; i++, k--) {
            narrowed.set(k, keySet.get(56-PermutationTables.narrowKey[i]));
        }
        return narrowed.toLongArray()[0];
    }

    private long getRoundKey(int c, int d) {
        return narrowKey(((long)c << 28) | d);
    }

    private long[] oneRound(long[] array, long key) {
        for (int i = 0; i < array.length; i++) {
            long left = array[i] >>> 32;
            long right = (array[i] << 32) >>> 32;
            BitSet bitSet = extendBlock(right);
            BitSet keySet = BitSet.valueOf(new long[] {key});
            bitSet.xor(keySet);
            bitSet = sBox(bitSet);
            bitSet = pBox(bitSet);
            array[i] = right << 32 | bitSet.toLongArray()[0] ^ left;
        }
        return array;
    }

    private byte[] code(byte[] byteDate, long key, boolean decode) {
        byteDate = ArrayUtils.extendSize(byteDate);
        long[] blocks = ArrayUtils.separateBitsToLong(byteDate);
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = initPermutation(blocks[i]);
        }
        int c = getC(key);
        int d = getD(key);
        for (int i = 0; i < 16; i++) {
            c = shift(c, i, decode);
            d = shift(d, i, decode);
            long roundKey = getRoundKey(c,d);
            oneRound(blocks, roundKey);
        }
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = this.finPermutation(blocks[i]);
        }
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
