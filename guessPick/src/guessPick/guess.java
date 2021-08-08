
package guessPick;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;


public class guess {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
		// TODO Auto-generated method stub
		System.out.print("hello, world\n");
		
		// pick a number
		// 1: Read CSV file for weightings
		
		// 2: Compute the field of values
		
		// 3: Prepare a round of 5 picks and a power ball
		
		
		// 4: Tally stats and show the preferred picks
		SecureRandom r = fipsUtils.buildDrbg();
		byte[] b = new byte[32];
		for (int i=0; i<10000000; i++) {
			r.nextBytes(b);
			System.out.printf("random: %d, ", i);
			System.out.println(Arrays.toString(b));
		}
		System.out.print("Done");
	}

}
