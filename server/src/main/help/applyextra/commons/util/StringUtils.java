package applyextra.commons.util;

/**
 * A utility class that assists in manipulating and handling String operations.
 */
public final class StringUtils {

    private StringUtils() {}

    /**
     * Tests whether the given string can be evaluated to true.
     * @param str The string to be converted to a true value
     * @return Returns true if the given string is 'yes', 'y', 'true' or '1'.
     */
    public static boolean isTrue(String str) {
        return "yes".equalsIgnoreCase(str) ||
                "y".equalsIgnoreCase(str) ||
                "true".equalsIgnoreCase(str) ||
                "1".equalsIgnoreCase(str);
    }

    /**
     * Tests whether the given string can be evaluated to false.
     * @param str The string to be converted to a false value
     * @return Returns true if the given string is null, 'no', 'n', 'false' or '0'.
     */
    public static boolean isFalse(String str) {
        return str == null ||
                "no".equalsIgnoreCase(str) ||
                "n".equalsIgnoreCase(str) ||
                "false".equalsIgnoreCase(str) ||
                "0".equalsIgnoreCase(str);
    }

    /**
     * Tests whether the given string is null or empty.
     * @param str
     * @return true if the string is null or empty, else false.
     */
    public static boolean isEmpty(String str) {
        return org.apache.commons.lang.StringUtils.isEmpty(str);
    }
}
