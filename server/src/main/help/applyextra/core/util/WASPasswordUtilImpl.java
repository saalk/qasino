package applyextra.core.util;

import applyextra.core.exception.RIAFRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Class for encoding and decoding WebSphere passwords
 * It's used as an alternative for clear passwords in property files.
 */
public class WASPasswordUtilImpl implements PasswordUtil {

	private static final String XOR = "{xor}";
	private static final int MAGIC = 0x5F;

	/**
	 * Decodes a password using the WAS password encoding/decoding scheme
	 * @param input WAS encoded string
	 * @return clear text password
     */
	@Override
	public String decode(final String input) {
		if (StringUtils.isEmpty(input)) {
			throw new RIAFRuntimeException("Cannot decode empty input string");
		}
		if (!input.startsWith(XOR)) {
			throw new RIAFRuntimeException("Wrong format while decoding value. Make sure the is {xor}'ed!");
		}

		try {
			final String encodedString = input.substring(XOR.length());

			byte[] encodedBytes = Base64.decodeBase64(encodedString);
			for (int i = 0; i < encodedBytes.length; i++) {
				encodedBytes[i] ^= MAGIC;
			}
			return new String(encodedBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RIAFRuntimeException(e);
		}
	}


	/**
 	 * Encodes a clear text password using the WAS password encoding/decoding scheme
	 * @param input clear text password
	 * @return WAS encoded password
     */
	@Override
	public String encode(final String input) {

		if (input == null) {
			throw new RIAFRuntimeException("Cannot encode null input string");
		}

		try {
			byte[] bytes = input.getBytes("UTF-8");
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] ^= MAGIC;
			}

			byte[] encodedBytes = Base64.encodeBase64(bytes);

			return "{xor}" + new String(encodedBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RIAFRuntimeException(e);
		}
	}
	
}
