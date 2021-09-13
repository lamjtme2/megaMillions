package guessPick;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class guessCommon {
	
	public static Integer[][] LoadLastNums(Integer n, String file) throws IOException {
		Path stats = Paths.get(file);
		System.out.printf("File: %s\n", stats.toAbsolutePath().toString());
		Integer[][] a = new Integer[n][6];
		
		Pattern p = Pattern.compile("Mega Ball: (\\d+)");
		List<String[]> lines = Files.lines(stats.toAbsolutePath())
				.map(line -> line.split("; "))
				.collect(Collectors.toList());
		int k=0;
		for (int i=0; i<lines.size(); i++) {
			// for every line in file
			String[] semiFrag = lines.get(i);
			if (semiFrag.length < 3 || k >= n)
				continue;
			String[] balls = semiFrag[1].trim().split(",");
			for (int j=0; j<5; j++)
				a[k][j] = Integer.parseInt(balls[j]);
			String mballStatement = semiFrag[2].trim();
			Matcher m = p.matcher(mballStatement);
			if (m.find()) {
				a[k][5] = Integer.parseInt(m.group(1));
				k++;
			} else {
				throw new IOException("Bad file input format");
			}
		}
		
		System.out.println("\nLast numbers:");
		for (int i=0; i<a.length; i++) {
			System.out.printf("%03d: ", i);
			for (int j=0; j<a[0].length; j++)
				System.out.printf("%02d - ", a[i][j]);
			System.out.println("");
		}
		return a;
	}
	
	public static Long[] LoadStats(String file) throws IOException {
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
	
	public static List<Integer> PickNums(Integer byteSpread, Long[] nSpread, int nPicks) {
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
