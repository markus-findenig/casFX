package tests;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class EncryptionTest {
	/**
	 * Zwischengespeichertes Schluesselpaar
	 */
	private static KeyPair keyPair;

	/**
	 * Erzeugt ein RSA Schluesselpaar
	 *
	 * @return RSA Schluesselpaar
	 * @throws Exception
	 */
	public static KeyPair getKeyPair() throws Exception {
		if (keyPair == null) {
			KeyPairGenerator kpg;
			try {
				kpg = KeyPairGenerator.getInstance("RSA");
				kpg.initialize(512); // 2048
				keyPair = kpg.generateKeyPair();
			} catch (NoSuchAlgorithmException e) {
				throw new Exception("Fehler beim Erzeugen des Schluesselpaars: " + e.getMessage());
			}
		}
		return keyPair;
	}

	/**
	 * Wrapped einen OutputStream in einen verschlüsselnden CipherOutputStream
	 *
	 * @param os
	 *            OutputStream
	 * @return verschlüsselter OutputStream
	 * @throws Exception
	 */
	public static OutputStream encryptOutputStream(OutputStream os) throws Exception {
		try {
			// temporaeren AES Key erzeugen
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			SecureRandom random = new SecureRandom();
			keygen.init(random);
			SecretKey key = keygen.generateKey();
			// mit RSA verschluesseln und an Empfaenger senden
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.WRAP_MODE, getKeyPair().getPublic());
			byte[] encryptedAesKey = cipher.wrap(key);
			os.write(encryptedAesKey);
			// eigentliche Nachricht mit AES verschluesseln
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			os = new CipherOutputStream(os, cipher);
		} catch (Exception e) {
			throw new Exception("Fehler beim Verschluesseln: " + e.getMessage());
		}
		return os;
	}

	/**
	 * Liest den Inhalt einer Datei aus.
	 *
	 * @param file
	 *            Dateiname
	 * @return Dateiinhalt (erste Zeile)
	 * @throws Exception
	 */
	public static String readFile(String file) throws Exception {
		InputStream is = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader isrb = new BufferedReader(isr);
		return isrb.readLine();
	}

	/**
	 * Wrapped einen InputStream in einen entschlüsselnden CipherInputStream
	 *
	 * @param is
	 *            InputStream
	 * @return entschlüsselnder Inputstream
	 * @throws Exception
	 */
	public static InputStream decryptInputStream(InputStream is) throws Exception {
		try {
			// AES Key lesen
			byte[] wrappedKey = new byte[64];	// 256
			is.read(wrappedKey, 0, 64);
			// AES Key mit RSA entschluesseln
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.UNWRAP_MODE, getKeyPair().getPrivate());
			Key key = cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);
			// Daten mit AES entschluesseln
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			is = new CipherInputStream(is, cipher);
		} catch (Exception e) {
			throw new Exception("Fehler beim Entschluesseln: " + e.getMessage());
		}
		return is;
	}

	/**
	 * Beispiel für Verschlüsselung mit CipherInputStream und CipherOutputStream
	 *
	 * @param args
	 */
	public static void main(String... args) {
		try {
			String file = "c:/temp/test.txt";
			String plain = "test";
			System.out.println("Klartext: " + plain);
			// verschlüsseln
			OutputStream os = new FileOutputStream(file);
			os = encryptOutputStream(os);
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write(plain);
			osw.close();
			// verschlüsselten Text ausgeben
			String secret = readFile(file);
			System.out.println("verschluesselter Text: " + secret);
			// entschlüsseln
			InputStream is = new FileInputStream(file);
			is = decryptInputStream(is);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader isrb = new BufferedReader(isr);
			String decryptedPlain = isrb.readLine();
			isrb.close();
			System.out.println("entschluesselter Text: " + decryptedPlain);
			
			System.out.println();
			System.out.println("Public: " + getKeyPair().getPublic());
			System.out.println();
			System.out.println("Private: " + getKeyPair().getPrivate());
			
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}