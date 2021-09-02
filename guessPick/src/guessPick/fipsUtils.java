package guessPick;

import java.security.SecureRandom;

import org.bouncycastle.crypto.EntropySourceProvider;
import org.bouncycastle.crypto.fips.FipsDRBG;
import org.bouncycastle.crypto.util.BasicEntropySourceProvider;
import org.bouncycastle.util.Strings;

public class fipsUtils {
	public static SecureRandom buildDrbg()
	{
		EntropySourceProvider entSource = new BasicEntropySourceProvider(new SecureRandom(), true);
		FipsDRBG.Builder drgbBldr = FipsDRBG.SHA512_HMAC.fromEntropySource(entSource)
		//		.setSecurityStrength(128)
		//		.setEntropyBitsRequired(128);
		 .setSecurityStrength(256)
		 .setEntropyBitsRequired(256);
		return drgbBldr.build(Strings.toByteArray("I love kate lam"), false);
	}
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static long bytesToLong(byte[] bytes) {
		long value = 0;
		for (int i = 0; i < bytes.length; i++)
		{
		   value += ((long) bytes[i] & 0xffL) << (8 * i);
		}
		return value;
	}
	
	public static Integer spreadToIndex(Long[] spread, long spreadValue) {
		Integer i;
		for (i=0; i<spread.length; i++) {
			long comp = spread[i];
			if (spreadValue < comp)
				break;
		}
		return i;
	}
}
