package tests;

import java.security.SecureRandom;

public class RandomGenerator {

	public static void main(String[] args) {
		
		String cw;
		int cwLenght = 16; // in Hex 16*4 = 64 Bit
		SecureRandom randomService = new SecureRandom();
		//System.out.println(randomService.nextInt());
		StringBuilder sb = new StringBuilder();
		while (sb.length() < cwLenght) {
			sb.append(Integer.toHexString(randomService.nextInt()));
		}
		sb.setLength(cwLenght);
		cw = sb.toString().toUpperCase();
		System.out.println(cw);
		
		
		
	}

}
