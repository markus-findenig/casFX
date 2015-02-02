package tests;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class ApiSecurityExample {
	public static void main(String[] args) {
		try {
			//String ak0 = "1234567812345678";
			String secret = "0123456789ABCDEF";
			String message = "This is a confidential message. This is a confidential message.";

			byte[] key = secret.getBytes();
			
			// HmacSHA1, HmacSHA256
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec secret_key = new SecretKeySpec(key, mac.getAlgorithm());
			mac.init(secret_key);
			
			byte[] macbytes = mac.doFinal(message.getBytes());

			String out = null;
			
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < sb.length(); i++) {
				sb.append(macbytes[i]);
			}
			//sb.setLength(8);
			
			System.out.println(sb.toString().toUpperCase());
			
			
			for (int j = 0; j < macbytes.length; j++) {
				System.out.format("%02X ", macbytes[j]);
			}
			System.out.println();
			System.out.println("Hex.encodeHexString( message.getBytes()"+ Hex.encodeHexString( message.getBytes() ) );
			
			String hash = Base64.encodeBase64String(mac.doFinal(message.getBytes()));
			
			System.out.println(macbytes);
			
			System.out.println(secret_key);
			System.out.println(hash);
						
			
			String foo = "I am a string";
			byte[] bytes = foo.getBytes();
			System.out.println( Hex.encodeHexString( bytes ) );
			
			
		} catch (Exception e) {
			System.out.println("Error");
		}
	}
}
