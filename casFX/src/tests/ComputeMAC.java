package tests;

import javax.crypto.Mac;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.Base64;

public class ComputeMAC {
	public static void main(String[] unused) throws Exception {
		String datafile = "src\\tests\\ComputeMAC.java";

		KeyGenerator kg = KeyGenerator.getInstance("HmacSHA1");

		SecretKey key = kg.generateKey();
		
		
		System.out.println("Key: " + key);

		Mac mac = Mac.getInstance(key.getAlgorithm());
		mac.init(key);

		FileInputStream fis = new FileInputStream(datafile);
		byte[] dataBytes = new byte[1024];
		int nread = fis.read(dataBytes);
		while (nread > 0) {
			mac.update(dataBytes, 0, nread);
			nread = fis.read(dataBytes);
		}
		;
		byte[] macbytes = mac.doFinal();
		
		

		System.out.println(Integer.toHexString(macbytes[0]));

		for (int j = 0; j < macbytes.length; j++) {
			System.out.format("%02X ", macbytes[j]);
		}
		System.out.println();

		String string = macbytes.toString();

		String out = String.format("%02x ", new BigInteger(1, string.getBytes("UTF-8")));

		System.out.println("MAC(lenght):: " + macbytes.length);
		System.out.println("MAC(in hex):: " + out);
		
		
		
		String message = "This is a confidential message";
		String stringKey = "00112233445566778899AABBCCDDEEFF";
		
		byte[] decodedKey = Base64.getDecoder().decode(stringKey);
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA1");
		System.out.println("SecretKey: " + originalKey);
		
		// ALG_DES_MAC4_ISO9797_M1
		Mac mac2 = Mac.getInstance(originalKey.getAlgorithm());
		mac2.init(originalKey);
		
		// get the string as UTF-8 bytes
		byte[] b = message.getBytes("UTF-8");
		
		// create a digest from the byte array
		byte[] digest = mac2.doFinal(b);
		
		System.out.println("mac2: " + digest);
		
		//String string2 = digest.toString();
		
		String string2 = digest.toString().substring(4, 8);
		
		String out2 = String.format("%02X", new BigInteger(1, string2.getBytes("UTF-8")));

		System.out.println("MAC(lenght):: " + digest.length);
		System.out.println("MAC(in hex):: " + out2);
		
		
		
		
		
		
	}
}