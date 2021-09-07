
package guessPick;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
		Long nIncrement = Long.valueOf(args[2]);
		Long nTotal = Long.valueOf(args[3]);
		
		Long[] nCnt = new Long[nSpread.length];
		Long[] nCntMega = new Long[nSpreadMega.length];
		for (int i=0; i<nCnt.length; i++)
			nCnt[i] = 0L;
		for (int i=0; i<nCntMega.length; i++)
			nCntMega[i] = 0L;
		
		java.awt.Toolkit.getDefaultToolkit().beep();
		
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
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyymmdd-HHmmss");
		String fnBase = String.format("%s",  dtf.format(LocalDateTime.now()));
		String fnTemp = String.format("%s/out/%s/temp", System.getProperty("user.dir"),fnBase);
		String fnPath = String.format("%s/out/%s", System.getProperty("user.dir"),fnBase);
				File theDir = new File(fnPath);
		if (!theDir.exists())
			theDir.mkdirs();
		String fn =  null;
		FileWriter  writer = null;
		PrintWriter printWriter = null;
		for (long k=0; k<nTotal; k++) {
			if (k % nIncrement == 0 || writer == null || printWriter == null)  {
				if (writer != null || printWriter != null) {
					printWriter.close();
					File f_old = new File(fnTemp);
					File f_new = new File(fn);
					if (f_old.renameTo(f_new))
						System.out.printf("Writing [%s]\n", fn);
					else {
						System.out.printf("Failed to rename to [%s]\n",  fn);
						throw new IOException("can't rename");
					}
				}
				fn = String.format("%s/%s-%010d.txt", fnPath,fnBase, k / nIncrement);
				writer = new FileWriter(fnTemp);
				printWriter = new PrintWriter(writer);
			}
			// pick plays
			printWriter.printf("%3d: ", k);
			List<Integer> nums = PickNums(byteSpread, nSpread, 5);
			Collections.sort(nums);
			for (int i=0; i<nums.size(); i++)
				printWriter.printf("%3d, ", nums.get(i)+1);
			List<Integer> numsMega = PickNums(byteSpread, nSpreadMega, 1);
			printWriter.printf("%6d\n", numsMega.get(0)+1);
			
			// record cnts
			for (Integer num: nums)
				nCnt[num]++;
			nCntMega[numsMega.get(0)]++;
			
			// display
//			if (k % 10000 == 9999)
//				PrintStats(nCnt, nCntMega);
		}
		printWriter.close();
		File f_old = new File(fnTemp);
		File f_new = new File(fn);
		f_old.renameTo(f_new);

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
		PrintStats(nCnt, nCntMega);

		System.out.print("\nDone");
	}

	private static void PrintStats(Long[] nCnt, Long[] nCntMega)  {
		// java.awt.Toolkit.getDefaultToolkit().beep();
		System.err.print("\nBalls:\n");
		for (int i=0; i<nCnt.length; i++)  {
			System.err.printf("%3d-%5d, ", i+1,nCnt[i]);
			if (i % 10 == 9)
				System.err.printf("\n");
		}
		System.err.print("\nMega Ball:\n");
		for (int i=0; i<nCntMega.length; i++)  {
			System.err.printf("%3d-%5d, ", i+1,nCntMega[i]);
			if (i % 10 == 9)
				System.err.printf("\n");
		}
	}
	
//	private static List<Integer[]> LoadPatterns(String file) throws IOException {
//		// need to load the original file for patterns
//		Path stats = Paths.get(file);
//		System.out.printf("File: %s\n", stats.toAbsolutePath().toString());
//		List<String[]> collect = Files.lines(stats.toAbsolutePath())
//				.map(line -> line.split(", "))
//				.collect(Collectors.toList());
//		Integer[] nCnt = new Integer[collect.size()];
//		List<Integer[]> ret = new ArrayList<Integer[]>();
//		for (int i=0; i<collect.size(); i++) {
//			String[] s = collect.get(i);
//			Integer ball = Integer.parseInt(s[0].trim());
//			Integer iterations = Integer.parseInt(s[1].trim());
//			nCnt[ball-1] = iterations;
//		}
//		return new ArrayList<Integer[]>();
//	}
		
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
	
	private static List<Integer> PickNums(Integer byteSpread, Long[] nSpread, int nPicks) {
		SecureRandom r = fipsUtils.buildDrbg();
		byte[] b = new byte[(int)byteSpread];
		List<Integer> picks = new ArrayList<>();
		while (picks.size() < nPicks) {
			r.nextBytes(b);
			Long spreadValue = fipsUtils.bytesToLong(b);
			Integer newIndex = ((Integer)fipsUtils.spreadToIndex(nSpread, spreadValue));
			if (!picks.contains(newIndex))
				picks.add(newIndex);
		}
		//Collections.sort(picks);
		return picks;
	}
}
