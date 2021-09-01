
package guessPick;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.*;

public class guess {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
		// TODO Auto-generated method stub
		System.out.print("hello, world\n");
		
		// pick a number
		// 1: Read CSV file for weightings
		Long[] nSpread = LoadStats(args[0]);
		Long[] nSpreadMega = LoadStats(args[1]);
		
//		Path stats = Paths.get(args[0]);
//		System.out.printf("File: %s\n", stats.toAbsolutePath().toString());
//		List<String[]> collect = Files.lines(stats.toAbsolutePath())
//				.map(line -> line.split(", "))
//				.collect(Collectors.toList());
//		Integer[] nCnt = new Integer[collect.size()];
//		for (int i=0; i<collect.size(); i++) {
//			String[] s = collect.get(i);
//			Integer ball = Integer.parseInt(s[0].trim());
//			Integer iterations = Integer.parseInt(s[1].trim());
//			nCnt[ball-1] = iterations;
//		}
//		
//		// cumulative increments
//		Integer[] nIncr = new Integer[collect.size()];
//		Integer totalIterations = 0;
//		for (int i=0; i<collect.size(); i++) {
//			totalIterations += nCnt[i];
//			nIncr[i] = totalIterations;
//			System.out.printf("nIncr[%d]: %d (+%d)\n", i,totalIterations, nCnt[i]);
//		}
//		System.out.printf("totalIterations: %d\n", totalIterations);
//		
//		// 2: Compute the field of values
//		Integer byteSpread = 3;
//		Double scale = Math.pow(256,byteSpread);
//		Long[] nSpread = new Long[collect.size()];
//		for (int i=0; i<collect.size(); i++) {
//			Long width = Double.valueOf(nIncr[i]*scale/totalIterations).longValue();
//			nSpread[i] = width;
//			System.out.printf("Spread[%d]: %d\n", i,nSpread[i]);
//		}
//		
		Integer byteSpread = 3;
		for (int k=0; k<10000000; k++) {
			System.out.printf("%3d: ", k);
			List<Integer> nums = PickNums(byteSpread, nSpread, 5);
			Collections.sort(nums);
			for (int i=0; i<nums.size(); i++)
				System.out.printf("%3d, ", nums.get(i));
			List<Integer> numsMega = PickNums(byteSpread, nSpread, 1);
			System.out.printf("%6d\n", numsMega.get(0));
		}
		
		// 3: Prepare a round of 5 picks and a power ball
//		SecureRandom r = fipsUtils.buildDrbg();
//		byte[] b = new byte[byteSpread];
//		for (int i=0; i<100000; i++) {
//			r.nextBytes(b);
//			System.out.printf("random: %d, ", i);
//			//ByteBuffer bb = ByteBuffer.wrap(b);
//			
//			Long spreadValue = fipsUtils.bytesToLong(b);
//			System.out.printf(": %s, %d -> %d\n", fipsUtils.bytesToHex(b), spreadValue, fipsUtils.spreadToIndex(nSpread, spreadValue));
//		}
		
		// 4: Tally stats and show the preferred picks
		
		System.out.print("\nDone");
	}

	private static Long[] LoadStats(String file) throws IOException {
		Path stats = Paths.get(file);
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
			System.out.printf("nIncr[%d]: %d (+%d)\n", i,totalIterations, nCnt[i]);
		}
		System.out.printf("totalIterations: %d\n", totalIterations);
		
		// 2: Compute the field of values
		Integer byteSpread = 3;
		Double scale = Math.pow(256,byteSpread);
		Long[] nSpread = new Long[collect.size()];
		for (int i=0; i<collect.size(); i++) {
			Long width = Double.valueOf(nIncr[i]*scale/totalIterations).longValue();
			nSpread[i] = width;
			System.out.printf("Spread[%d]: %d\n", i,nSpread[i]);
		}
		
		return nSpread;
	}
	
	private static <Integer> List<Integer> PickNums(Integer byteSpread, Long[] nSpread, int nPicks) {
		SecureRandom r = fipsUtils.buildDrbg();
		byte[] b = new byte[(int)byteSpread];
		List<Integer> picks = new ArrayList<>();
		while (picks.size() < nPicks) {
			r.nextBytes(b);
			Long spreadValue = fipsUtils.bytesToLong(b);
			@SuppressWarnings("unchecked")
			Integer newIndex = ((Integer)fipsUtils.spreadToIndex(nSpread, spreadValue));
			if (!picks.contains(newIndex))
				picks.add(newIndex);
		}
		//Collections.sort(picks);
		return picks;
	}
}
