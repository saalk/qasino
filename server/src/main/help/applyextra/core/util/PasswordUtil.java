package applyextra.core.util;

/**
 * Encodes and decodes a password.
 * It's used as an alternative for clear passwords in property files.
 */
public interface PasswordUtil {

	/**
	 * Decodes a password
	 * @param input encoded string
	 * @return output clear text password
	 */
	String decode(final String input);

	/**
	 * Encodes a clear text password some encoded password
	 * @param input clear text password
	 * @return some encoded password
	 */
	String encode(final String input);

}