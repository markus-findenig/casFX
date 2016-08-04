package tests;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class GenerateMessageAuthenticationCode {

	public static void main(String[] args) {

		try {
			
			// AK 00
			String macKey = "465284AA69A329782CA898EB3701F546";

			// get a key generator for the HMAC-SHA256 keyed-hashing algorithm
			//KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA1");

			// generate a key from the generator
			//SecretKey key = keyGen.generateKey();
			SecretKeySpec key = new SecretKeySpec(macKey.getBytes(), "HmacSHA1");

			// create a MAC and initialize with the above key
			Mac mac = Mac.getInstance(key.getAlgorithm());
			mac.init(key);

			String message = "8000000000000000BBFF000123456789ABCDEF0123456789ABCDEFC80804115931D50000000000";

			// get the string as UTF-8 bytes
			byte[] b = message.toString().getBytes(Charset.forName("UTF-8"));

			// create a digest from the byte array
			byte[] digest = mac.doFinal(b);
			
			
			char[] test = Hex.encodeHex(digest);
			
			String s = String.valueOf(Hex.encodeHex(digest)).substring(0, 8).toUpperCase();
			
			
		
			
			System.out.println("test" + test);
			
			System.out.println("s:" + s);
						
			// cut lsb to 4 bytes in hex
			String macString = String.format("%02X", new BigInteger(1, digest.toString().substring(4, 8).getBytes("UTF-8")));
			System.out.println("macString : " + macString);
			
			for (int j = 0; j < digest.length; j++) {
				System.out.format("%02X ", digest[j]);
			}
			System.out.println();
			
			
			for (int j = 0; j < 4; j++) {
				System.out.format("%02X ", digest[j]);
			}
			System.out.println();
					
			System.out.println(message);
			System.out.println(String.valueOf(digest));
			
			byte[] cutDigest = Arrays.copyOfRange(digest, 0, 4);
			System.out.println(Hex.encodeHexString(cutDigest).toUpperCase());
			
			System.out.println(Hex.encodeHexString(digest).toUpperCase());
			
			System.out.println("MacLength: " + mac.getMacLength() + " bytes");

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