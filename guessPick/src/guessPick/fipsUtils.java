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
		.setSecurityStrength(256)
		.setEntropyBitsRequired(256);
		return drgbBldr.build(Strings.toByteArray("number only used once"), false);
	}
}
