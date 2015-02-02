package tests;

import javax.crypto.Mac;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.io.FileInputStream;
import java.math.BigInteger;

public class ComputeMAC {
	public static void main(String[] unused) throws Exception {
		String datafile = "src\\tests\\ComputeMAC.java";

		KeyGenerator kg = KeyGenerator.getInstance("HmacSHA1");

		SecretKey key = kg.generateKey();

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

	}
}