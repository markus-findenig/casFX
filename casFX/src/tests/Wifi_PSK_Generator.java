package tests;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Wi-Fi Protected Access (WPA)
 * Pre-shared key (PSK) Generator
 * 
 * http://jorisvr.nl/wpapsk.html
 * 
 * WIFI_WPA_PMK - PMK (Pairwise Master Key)
 * 
 */
public class Wifi_PSK_Generator {

	public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException {

		String ssid = "one";
		String passphrase = "password";

		//String Key = PBKDF2(passphrase, ssid, 4096, 256);
		
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		char[] pass = passphrase.toCharArray();
		byte[] salt = ssid.getBytes();
		KeySpec ks = new PBEKeySpec(pass ,salt, 4096, 256);
		SecretKey s = f.generateSecret(ks);
		Key k = new SecretKeySpec(s.getEncoded(),"AES");
		
		System.out.println("SSID = " + ssid );
		System.out.println("Passphrase = " + passphrase );
		
		System.out.print("PSK = ");
		for (int j = 0; j < k.getEncoded().length; j++) {
			System.out.format("%02x", k.getEncoded()[j]);
		}
		System.out.println();
				
		
	}

}
