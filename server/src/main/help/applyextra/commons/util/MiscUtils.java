package applyextra.commons.util;

import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

public final class MiscUtils {

	private MiscUtils() {
	}

	/**
	 * We need to keep userId and requestToken same, and current XSD only allows 8 digit userId. We
	 * also append RQ to userId, Hence we only need 6 random characters.
	 *
	 * @return 6 Digit random String
	 */
	public static String getSixHexDigitRandom() {
		final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		int start = new SecureRandom().nextInt(18);
		int end = start + 6;
		return uuid.substring(start, end);
	}

	public static Properties loadProperties(final String filename, final Class<?> clazz) throws IOException {
		Properties result = new Properties();
		final ClassLoader classLoader = clazz.getClassLoader();

		if (classLoader != null) {
			final InputStream resourceAsStream = classLoader.getResourceAsStream(filename + ".properties");

			if (resourceAsStream == null) {
				throw new FileNotFoundException("could not read from database.properties");
			}
			try {
				result.load(resourceAsStream);
			} finally {
				resourceAsStream.close();
			}

		} else {
			String errorMessage = "could not get class loader from data source";
			throw new DataSourceLookupFailureException(errorMessage);
		}
		return result;
	}

    public static void validate(final Object field, final Validator validator) {
        Set<ConstraintViolation<Object>> violations = validator.validate(field);
        if(!violations.isEmpty()) {
            throw new WebApplicationException(violations.iterator().next().getMessage(), Response.Status.BAD_REQUEST);
        }
    }
}
