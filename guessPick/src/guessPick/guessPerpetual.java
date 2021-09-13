package guessPick;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class guessPerpetual {

	protected static Long[] nSpread;
	protected static Long[] nSpreadMega;
	protected static Long nIncrement;
	
	protected static Long[] nCnt;
	protected static Long[] nCntMega;

	protected static Integer[][] last;
	
	protected static Integer byteSpread = 3;

	protected static String fnBase;
	protected static String fnTemp;
	protected static String fnPath;
	protected static DateTimeFormatter dtf;


	private static void LoadInfo(String arg0, String arg1, String arg2, String arg3, String arg4) throws IOException {
		nSpread = guessCommon.LoadStats(arg0);
		nSpreadMega = guessCommon.LoadStats(arg1);
		nIncrement = Long.valueOf(arg2);
		last = guessCommon.LoadLastNums(Integer.valueOf(arg3), arg4);
		
		nCnt = new Long[nSpread.length];
		nCntMega = new Long[nSpreadMega.length];
		for (int i=0; i<nCnt.length; i++)
			nCnt[i] = 0L;
		for (int i=0; i<nCntMega.length; i++)
			nCntMega[i] = 0L;
		
		java.awt.Toolkit.getDefaultToolkit().beep();
	}
	
	private static void DoWork() {
		String sBar = "------";
		String s;
		List<String> ot = new ArrayList<>();
		Integer[] spanMax = new Integer[] { 0, 0, 0, 0, 0, 0 };
		for (int i=0; i<nIncrement; i++) {
			
			List<Integer> nums = guessCommon.PickNums(byteSpread, nSpread, 5);
			Collections.sort(nums);
			List<Integer> mb = guessCommon.PickNums(byteSpread, nSpreadMega, 1);
			
			int rowMatchMax = 0;
			int rowMatch;
			for (int h=0; h<last.length; h++) {
				// for every historical record row
				rowMatch = 0;
				for (int j=0; j<5; j++) {
					// for every historical column
					if (nums.get(j) == last[h][j])
						rowMatch++;
				}
				if (mb.get(0) == last[h][5])
					rowMatch++;
				if (rowMatch > rowMatchMax)
					rowMatchMax = rowMatch;
			}
			if (rowMatchMax > 0)
				spanMax[rowMatchMax-1]++;
			s = String.format("%06d: %02d, %02d, %02d, %02d, %02d, %02d, Hits: %s>%d",
					i,
					nums.get(0),nums.get(1),nums.get(2),nums.get(3),nums.get(4),
					mb.get(0),
					sBar.repeat(rowMatchMax),rowMatchMax);
			ot.add(s);
			//System.err.println(s);
		}

		String score = String.format("(%6d,%6d,%6d,%6d,%6d)",
				spanMax[5],spanMax[4],spanMax[3],spanMax[2],spanMax[1]);

		// save only scores of 5+ matches
		if (spanMax[5] > 0 || spanMax[4] > 5) {
			// format the file name
			String fn = String.format("%s/%06d_%06d_%06d_%06d_%06d_%06d_%s.txt",
					fnPath,
					spanMax[5],spanMax[4],spanMax[3],spanMax[2],spanMax[1],spanMax[0],
					new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
			
			// write the sequence
			try {
				FileWriter  writer = new FileWriter(fnTemp);
				PrintWriter printWriter = new PrintWriter(writer);
				for (int m=0; m<ot.size(); m++)
					printWriter.println(ot.get(m));
				printWriter.close();
				File f_old = new File(fnTemp);
				File f_new = new File(fn);
				f_old.renameTo(f_new);
			} catch(IOException e) {
				System.err.println("Unable to write fnTemp");
			}
			System.err.printf("Wrote [%s]\n", fn);
		} else {
			System.err.printf("...poor yield %s\n", score);
		}
		
	}
	
    @SuppressWarnings("deprecation")
	public static void main(String[] args)  throws IOException {
    	LoadInfo(args[0],args[1],args[2],args[3],args[4]);

    	//dtf = DateTimeFormatter.ofPattern("yyyymmdd-HHmmss");
    	//fnBase = ""; //String.format("%s",  dtf.format(LocalDateTime.now()));
    	fnTemp = String.format("%s/out2/temp", System.getProperty("user.dir"));
    	fnPath = String.format("%s/out2", System.getProperty("user.dir"));
    	File theDir = new File(fnPath);
    	if (!theDir.exists())
    		theDir.mkdirs();

		Thread t = new  Thread(new Runnable() {
			public void run() {
				while (true) {
					DoWork();
				}
			}
		});
		
		t.start();
		do {
			int c = System.in.read();
			//System.out.printf("%c", c);
			if (c == 113) // 'q'
				break;
		} while (true);
		t.stop();

		System.out.print("\nDone");
		
	}

}
