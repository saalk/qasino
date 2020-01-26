package applyextra.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jndi.JndiTemplate;

import javax.naming.NamingException;

/**
 * Utility class for JNDI lookups.
 */
@Slf4j
public class JNDIUtilImpl extends AbstractJndiUtil implements JNDIUtil {
	
	private static final JndiTemplate JNDITEMPLATE = new JndiTemplate();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getValue(final String jndiKey) {

		String result;
		try {
			final Object foundObject = JNDITEMPLATE.lookup(jndiKey);
            result = foundObject.toString();
			if (log.isDebugEnabled()) {
				log.debug("Found parameter [{}] in jndi with value [{}]", jndiKey, encodeValueWhenNeeded(jndiKey, result));
			}
		} catch (NamingException e) {
			result = null;
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValueFromCell(final String jndiKey) {
		String result;
		try {
			result = JNDITEMPLATE.getContext().lookup("cell/legacyRoot/" + jndiKey).toString();
			if (log.isDebugEnabled()) {
				log.debug("Found cell parameter [{}] in jndi with value [{}]", jndiKey, encodeValueWhenNeeded(jndiKey, result));
			}
		} catch (NamingException e) {
			result = null;
		}
		return result;
	}
}