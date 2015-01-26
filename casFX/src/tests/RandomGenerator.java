package tests;

import java.security.SecureRandom;

public class RandomGenerator {

	public static void main(String[] args) {

		SecureRandom randomService = new SecureRandom();
		System.out.println(randomService.nextInt());
		StringBuilder sb = new StringBuilder();
		while (sb.length() < 8) {
			sb.append(Integer.toHexString(randomService.nextInt()));
		}
		sb.setLength(8);
		System.out.println(sb.toString().toUpperCase());
		
		
		
	}

}
