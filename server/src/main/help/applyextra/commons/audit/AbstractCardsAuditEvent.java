package applyextra.commons.audit;

import lombok.Getter;
import applyextra.commons.audit.impl.WhichWay;
import nl.ing.riaf.core.event.ApplicationEvent;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Abstract move class for sending events to the audit logging
 * <p/>
 * The severity must be set for this move.
 */
public abstract class AbstractCardsAuditEvent {

	@Getter
	private final ApplicationEvent.Severity severity;
	@Getter
	private final WhichWay whichWay;
	private final Properties header = new Properties();
	private static final char delimiter = ',';

	protected AbstractCardsAuditEvent(final ApplicationEvent.Severity severity, final WhichWay whichWay) {
		this.severity = severity;
		this.whichWay = whichWay;
	}

	/**
	 * Get a value from the key-value headers
	 *
	 * @param key
	 * @return value associated with this key
	 */
	public String getHeaderValue(final String key) {
		return header.getProperty(key);
	}

	/**
	 * Get a list of all keys names in the headers
	 *
	 * @return set of keys
	 */
	public Set<Object> getHeaderKeys() {
		return header.keySet();
	}

	/**
	 * Set a value in the key-value header. This will overwrite any previous value.
	 *
	 * @param key   a non-null string, not containing the delimiter
	 * @param value a non-null value
	 */
	public void setHeaderValue(final String key, final Object value) {
		if (key == null || key.contains("" + delimiter)) {
			throw new IllegalArgumentException("key may not be null or contain the character \"" + delimiter + "\"");
		}
		if (value == null) {
			throw new IllegalArgumentException("value may not be null");
		}
		header.setProperty(key, value.toString());
	}

	/**
	 * Create a comma-separated string from the headers
	 *
	 * @return comma-separated headers string
	 */
	public final String getCommaDelimitedHeaders() {
		StringBuilder stringBuilder = new StringBuilder();
		for (Map.Entry<Object, Object> entry : header.entrySet()) {
			final String key = entry.getKey().toString();
			final String value = entry.getValue().toString();
			stringBuilder.append(delimiter);
			stringBuilder.append(key);
			stringBuilder.append(':');
			if (value.indexOf(delimiter) > -1) {
				stringBuilder.append('"');
				stringBuilder.append(value);
				stringBuilder.append('"');
			} else {
				stringBuilder.append(value);
			}
		}
		if (stringBuilder.length() == 0) {
			return null;
		} else {
			stringBuilder.deleteCharAt(0); // remove first delimiter
			return stringBuilder.toString();
		}
	}

	/**
	 * Fill a map of move specific fields. These are added to the other required fields.
	 * Note: The whichWay and headers will already be added
	 *
	 * @param fields list to receive the fields
	 */
	public abstract void getSpecificFields(final Map<String, Object> fields);

}
