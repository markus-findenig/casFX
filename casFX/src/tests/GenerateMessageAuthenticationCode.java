package tests;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;

public class GenerateMessageAuthenticationCode {

	public static void main(String[] args) {

		try {

			// get a key generator for the HMAC-SHA256 keyed-hashing algorithm
			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA1");

			// generate a key from the generator
			SecretKey key = keyGen.generateKey();

			// create a MAC and initialize with the above key
			Mac mac = Mac.getInstance(key.getAlgorithm());
			mac.init(key);

			String message = "This is a confidential message. This is a confidential message.";

			// get the string as UTF-8 bytes
			byte[] b = message.getBytes("UTF-8");

			// create a digest from the byte array
			byte[] digest = mac.doFinal(b);
			
			for (int j = 0; j < digest.length; j++) {
				System.out.format("%02X ", digest[j]);
			}
			System.out.println();
					
			System.out.println(message);
			System.out.println(String.valueOf(digest));
			
			System.out.println(Hex.encodeHexString(digest).toUpperCase());

		} catch (NoSuchAlgorithmException e) {
			System.out.println("No Such Algorithm:" + e.getMessage());
			return;
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported Encoding:" + e.getMessage());
			return;
		} catch (InvalidKeyException e) {
			System.out.println("Invalid Key:" + e.getMessage());
			return;
		}

	}

}