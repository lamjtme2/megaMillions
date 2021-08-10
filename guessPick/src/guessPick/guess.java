
package guessPick;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.stream.*;

import org.bouncycastle.asn1.eac.UnsignedInteger;


public class guess {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
		// TODO Auto-generated method stub
		System.out.print("hello, world\n");
		
		// pick a number
		// 1: Read CSV file for weightings
		Path stats = Paths.get(args[0]);
		System.out.printf("File: %s\n", stats.toAbsolutePath().toString());
		List<String[]> collect = Files.lines(stats.toAbsolutePath())
				.map(line -> line.split(", "))
				.collect(Collectors.toList());
		Integer[] nCnt = new Integer[collect.size()];
		for (int i=0; i<collect.size(); i++) {
			String[] s = collect.get(i);
			Integer ball = Integer.parseInt(s[0].trim());
			Integer iterations = Integer.parseInt(s[1].trim());
			nCnt[ball-1] = iterations;
		}
		
		// cumulative increments
		Integer[] nIncr = new Integer[collect.size()];
		Integer totalIterations = 0;
		for (int i=0; i<collect.size(); i++) {
			totalIterations += nCnt[i];
			nIncr[i] = totalIterations;
			System.out.printf("nIncr[%d]: %d\n", i,totalIterations);
		}
		System.out.printf("totalIterations: %d\n", totalIterations);
		
		// 2: Compute the field of values
		Integer byteSpread = 4;
		Long[] nSpread = new Long[collect.size()];
		for (int i=0; i<collect.size(); i++) {
			// nSpread[i] = (Long)(nIncr[i]/totalIterations**Long.MAX_VALUE*2) + Long.MIN_VALUE);
			System.out.printf("Spread[%d]: %d\n", i,nSpread[i]);
		}
		
		// 3: Prepare a round of 5 picks and a power ball
		
		
		// 4: Tally stats and show the preferred picks
		SecureRandom r = fipsUtils.buildDrbg();
		byte[] b = new byte[byteSpread];
		for (int i=0; i<10; i++) {
			r.nextBytes(b);
			System.out.printf("random: %d, ", i);
			ByteBuffer bb = ByteBuffer.wrap(b);
			
			System.out.printf("[%d]: %s, %d\n" ,b.length,fipsUtils.bytesToHex(b), bb.getLong());
		}
		
		System.out.print("\nDone");
	}

}
