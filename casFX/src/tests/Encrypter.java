package tests;

/**
 * 
 * http://stackoverflow.com/questions/9496447/encryption-of-video-files
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;

public class Encrypter {
    private final static int IV_LENGTH = 16; // Default length with Default 128
                                                // key AES encryption
    private final static int DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE = 1024;

    private final static String ALGO_RANDOM_NUM_GENERATOR = "SHA1PRNG";
    private final static String ALGO_SECRET_KEY_GENERATOR = "AES";
    private final static String ALGO_VIDEO_ENCRYPTOR = "AES/CBC/PKCS5Padding";

    @SuppressWarnings("resource")
    public static void encrypt(SecretKey key, AlgorithmParameterSpec paramSpec, InputStream in, OutputStream out)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IOException {
        try {
            // byte[] iv = new byte[] { (byte) 0x8E, 0x12, 0x39, (byte) 0x9C,
            // 0x07, 0x72, 0x6F, 0x5A, (byte) 0x8E, 0x12, 0x39, (byte) 0x9C,
            // 0x07, 0x72, 0x6F, 0x5A };
            // AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
            Cipher c = Cipher.getInstance(ALGO_VIDEO_ENCRYPTOR);
            c.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            out = new CipherOutputStream(out, c);
            int count = 0;
            byte[] buffer = new byte[DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE];
            while ((count = in.read(buffer)) >= 0) {
                out.write(buffer, 0, count);
            }
        } finally {
            out.close();
        }
    }

    @SuppressWarnings("resource")
    public static void decrypt(SecretKey key, AlgorithmParameterSpec paramSpec, InputStream in, OutputStream out)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IOException {
        try {
            // byte[] iv = new byte[] { (byte) 0x8E, 0x12, 0x39, (byte) 0x9C,
            // 0x07, 0x72, 0x6F, 0x5A, (byte) 0x8E, 0x12, 0x39, (byte) 0x9C,
            // 0x07, 0x72, 0x6F, 0x5A };
            // AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
            Cipher c = Cipher.getInstance(ALGO_VIDEO_ENCRYPTOR);
            c.init(Cipher.DECRYPT_MODE, key, paramSpec);
            out = new CipherOutputStream(out, c);
            int count = 0;
            byte[] buffer = new byte[DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE];
            while ((count = in.read(buffer)) >= 0) {
                out.write(buffer, 0, count);
            }
        } finally {
            out.close();
        }
    }

//    public static void main(String[] args) {
//        File inFile = new File("D:\\Users\\Videos\\Test\\TheSimpsonsMovie1080pTrailer.mp4");
//        File outFile_enc = new File("D:\\Users\\Videos\\Test\\out_enc_TheSimpsonsMovie1080pTrailer.mp4");
//        File outFile_dec = new File("D:\\Users\\Videos\\Test\\out_dec_TheSimpsonsMovie1080pTrailer.mp4");
//
//        try {
//            //SecretKey key = KeyGenerator.getInstance(ALGO_SECRET_KEY_GENERATOR).generateKey();
//        	String k = "00112233445566778899AABBCCDDEEFF";
//        	//byte[] k = ("1234567812345678").getBytes();
//        	
//        	
//        	
//        	
//        	byte[] keyByte = hexStringToByteArray(k);
//        	
//        	
//        	
//        	//SecretKey key = new SecretKeySpec(k.getBytes(), ALGO_SECRET_KEY_GENERATOR);
//        			
//            //byte[] keyData = key.getEncoded();
//            SecretKey key = new SecretKeySpec(keyByte, 0, keyByte.length, ALGO_SECRET_KEY_GENERATOR); 
//            //if you want to store key bytes to db so its just how to //recreate back key from bytes array
//
//            byte[] iv = new byte[IV_LENGTH];
//            SecureRandom.getInstance(ALGO_RANDOM_NUM_GENERATOR).nextBytes(iv); // If
//                                                                                // storing
//                                                                                // separately
//            AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
//            
//            System.out.println(k.toString());
//            System.out.println(key);
//            //System.out.println(key2);
//
//            Encrypter.encrypt(key, paramSpec, new FileInputStream(inFile), new FileOutputStream(outFile_enc));
//            Encrypter.decrypt(key, paramSpec, new FileInputStream(outFile_enc), new FileOutputStream(outFile_dec));
//        
//        
//        
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
    
    
    public static void main(String[] args) throws Exception {
        final String keyHex = "00000000000000000000000000123456";
        final String plaintextHex = "";

        SecretKey key = new SecretKeySpec(DatatypeConverter
            .parseHexBinary(keyHex), "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] result = cipher.doFinal(DatatypeConverter
            .parseHexBinary(plaintextHex));

        System.out.println(DatatypeConverter.printHexBinary(result));
      }
    
    
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

}
