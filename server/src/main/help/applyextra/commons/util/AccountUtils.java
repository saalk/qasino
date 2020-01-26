package applyextra.commons.util;

import java.math.BigInteger;

/**
 * A utility class for converting iban to bban
 */
public final class AccountUtils {
    private static final int BBAN_LENGTH = 10;

    private AccountUtils() {}

    /**
     * @param iban
     * @return Integer bban
     */
    public static BigInteger ibanToBbanAsInteger(String iban) {
        return new BigInteger(ibanToBban(iban));
    }

    /**
     *
     * @param iban
     * @return String bban (without 0's infront)
     */
    public static String ibanToBbanAsString(String iban) {
        return ibanToBbanAsInteger(iban).toString();
    }

    /**
     * This method returns a bban but has 0's infront if the bban is shorter than 10 digits,
     * use ibanToBbanAsString to convert properly
     */
    private static String ibanToBban(String iban) {
        return iban.substring(iban.length() - BBAN_LENGTH);
    }
}
