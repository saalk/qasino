package applyextra.core.util;

import applyextra.core.exception.RIAFRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

import static java.lang.String.format;

/**
 * Abstract Jndi Util class. This class has methods to gracefully convert string values (with or without defaults)
 * to doubles, longs, integers and booleans.
 * <p>
 * Super classes only need to define how to get a value for a given key for normal jndi values and for cell values.
 */
public abstract class AbstractJndiUtil implements JNDIUtil {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractJndiUtil.class);

    private static final String[] PASSWORD_KEYS = {"password", "passwd", "secret", "encodingkey", "encryptionkey"};
    private static final String ENCODED_PASSWORD_VALUE = "********";

    private static final String INTEGER_TYPE = "integer";
    private static final String DOUBLE_TYPE = "double";
    private static final String BOOLEAN_TYPE = "boolean";
    private static final String LONG_TYPE = "long";
    private static final String ENCODING_PREFIX = "{xor}";

    @Autowired
    private PasswordUtil passwordUtil;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJndiValue(final String jndiName) {
        return getJndiValue(jndiName, true);
    }

    /**
     * Get the value that belongs to a key
     *
     * @param jndiKey the key whose jndi value (normal scope) needs to be known
     * @return the value if it exists, or null if it doesn't
     */
    protected abstract String getValue(final String jndiKey);

    /**
     * Get the cell value that belongs to a key
     *
     * @param jndiKey the key whose jndi value (cell scope) needs to be known
     * @return the value if it exists, or null if it doesn't
     */
    protected abstract String getValueFromCell(final String jndiKey);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJndiValueWithDefault(final String jndiName,
                                          final String defaultValue) {
        String currentValue = getValue(jndiName);
        if (currentValue == null && defaultValue != null) {
            logOptionalNotFound(jndiName, defaultValue);
            currentValue = defaultValue;
        }
        return currentValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJndiValueFromCell(final String jndiKey) {
        final String value = getValueFromCell(jndiKey);
        if (value == null) {
            LOG.error("RIAF-0024: JNDI-param [{}] not found in cell scope", jndiKey);
            throw new RIAFRuntimeException("Required JNDI-parameter [" + jndiKey + "] not found in cell scope");
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJndiValue(final String jndiName, final boolean isMandatory) {
        String result = getValue(jndiName);
        if (result == null) {
            if (isMandatory) {
                LOG.error("RIAF-0023: Mandatory JNDI-parameter with key [{}] not found", jndiName);
                throw new RIAFRuntimeException("Required JNDI-parameter [" + jndiName + "] not found");
            } else {
                logOptionalNotFound(jndiName, null);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getJndiDoubleValue(final String jndiName, final boolean isMandatory) {
        return getJndiDouble(jndiName, isMandatory, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getJndiValueWithDefault(final String jndiKey, final double defaultValue) {
        return getJndiDouble(jndiKey, false, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJndiPassword(final String jndiKey) {
        final String value = getJndiValue(jndiKey, IS_MANDATORY);
        if (value.startsWith(ENCODING_PREFIX)) {
            return passwordUtil.decode(value);
        } else {
            throw new RIAFRuntimeException(
                    format("Password defined by JNDI property %s is not XOR-encoded", jndiKey));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getJndiLongValue(final String jndiName, final boolean isMandatory) {
        return getJndiLong(jndiName, isMandatory, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getJndiValueWithDefault(final String jndiName, final long defaultValue) {
        return getJndiLong(jndiName, false, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getJndiIntValue(final String jndiName, final boolean isMandatory) {
        return getJndiInt(jndiName, isMandatory, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getJndiValueWithDefault(final String jndiName, final int defaultValue) {
        return getJndiInt(jndiName, false, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getJndiBooleanValue(final String jndiName, final boolean isMandatory) {
        return getJndiBoolean(jndiName, isMandatory, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getJndiValueWithDefault(final String jndiName, final boolean defaultValue) {
        return getJndiBoolean(jndiName, false, defaultValue);
    }

    private Long getJndiLong(final String jndiName,
                             final boolean isMandatory,
                             final Long defaultValue) {
        final String currentValue = getJndiValue(jndiName, isMandatory);
        Long result = defaultValue;
        if (currentValue != null) {
            try {
                result = Long.parseLong(currentValue.trim());
            } catch (NumberFormatException e) {
                if (isMandatory) {
                    logMandatoryNotFound(jndiName, LONG_TYPE, currentValue);
                    throw new RIAFRuntimeException(e);
                } else {
                    if (defaultValue == null) {
                        logWarningIllegalValue(jndiName, currentValue, LONG_TYPE);
                    } else {
                        logWarningIllegalValue(jndiName, currentValue, defaultValue.toString(), LONG_TYPE);
                    }
                }
            }
        }
        return result;
    }

    private Integer getJndiInt(final String jndiName,
                               final boolean isMandatory,
                               final Integer defaultValue) {
        final String currentValue = getJndiValue(jndiName, isMandatory);
        Integer result = defaultValue;
        if (currentValue != null) {
            try {
                result = Integer.parseInt(currentValue.trim());
            } catch (NumberFormatException e) {
                if (isMandatory) {
                    logMandatoryNotFound(jndiName, INTEGER_TYPE, currentValue);
                    throw new RIAFRuntimeException(e);
                } else {
                    if (defaultValue == null) {
                        logWarningIllegalValue(jndiName, currentValue, INTEGER_TYPE);
                    } else {
                        logWarningIllegalValue(jndiName, currentValue, defaultValue.toString(), INTEGER_TYPE);
                    }
                }
            }
        }
        return result;
    }

    private Boolean getJndiBoolean(final String jndiName,
                                   final boolean isMandatory,
                                   final Boolean defaultValue) {
        final Boolean result;
        final String currentValue = getJndiValue(jndiName, isMandatory);
        if (currentValue == null) {
            logOptionalNotFound(jndiName, String.valueOf(defaultValue));
            result = defaultValue;
        } else {
            final String cleanValue = currentValue.trim().toLowerCase(Locale.ENGLISH);

            if (isTrueValue(cleanValue)) {
                result = true;
            } else if (isFalseValue(cleanValue)) {
                result = false;
            } else {
                if (isMandatory) {
                    logMandatoryNotFound(jndiName, BOOLEAN_TYPE, currentValue);
                    throw new RIAFRuntimeException(
                            "Mandatory JNDI-parameter with key [" + jndiName + "] is an illegal value [" + currentValue + "]");
                } else {
                    result = defaultValue;
                    if (defaultValue == null) {
                        logWarningIllegalValue(jndiName, currentValue, BOOLEAN_TYPE);
                    } else {
                        logWarningIllegalValue(jndiName, currentValue, defaultValue.toString(), BOOLEAN_TYPE);
                    }
                }
            }
        }
        return result;
    }

    private Double getJndiDouble(final String jndiName,
                                 final boolean isMandatory,
                                 final Double defaultValue) {
        final String currentValue = getJndiValue(jndiName, isMandatory);
        Double result = defaultValue;
        if (currentValue != null) {
            try {
                result = Double.parseDouble(currentValue.trim());
            } catch (NumberFormatException e) {
                if (isMandatory) {
                    logMandatoryNotFound(jndiName, DOUBLE_TYPE, currentValue);
                    throw new RIAFRuntimeException(e);
                } else {
                    if (defaultValue == null) {
                        logWarningIllegalValue(jndiName, currentValue, DOUBLE_TYPE);
                    } else {
                        logWarningIllegalValue(jndiName, currentValue, defaultValue.toString(), DOUBLE_TYPE);
                    }
                }
            }
        }
        return result;
    }

    private static boolean isFalseValue(final String cleanValue) {
        return "false".equals(cleanValue) || "0".equals(cleanValue);
    }

    private static boolean isTrueValue(final String cleanValue) {
        return "true".equals(cleanValue) || "1".equals(cleanValue);
    }


    private void logOptionalNotFound(final String jndiKey, final String defaultValue) {
        LOG.info("Optional JNDI-parameter with key [{}]] not found. Using default [{}]", jndiKey, defaultValue);
    }

    private void logWarningIllegalValue(final String jndiKey,
                                        final String currentValue,
                                        final String defaultValue,
                                        final String type) {
        LOG.warn("Optional JNDI-parameter with key [{}] contains an illegal {} value [{}]. Using defaultvalue [{}]",
                jndiKey, type, currentValue, defaultValue);
    }

    private void logWarningIllegalValue(final String jndiKey,
                                        final String currentValue,
                                        final String type) {
        LOG.warn("Optional JNDI-parameter with key [{}] contains an illegal {} value [{}].",
                jndiKey, type, currentValue);
    }

    private void logMandatoryNotFound(final String jndiName,
                                      final String type,
                                      final String currentValue) {
        LOG.error("RIAF-0025: Mandatory JNDI-parameter with key [{}] not a {} value [{}]",
                jndiName, type, currentValue);
    }

    /**
     * This method encodes passwords or returns the original value when the parameter/value does not contain a password.
     *
     * @param jndiKey key in jndi
     * @param value   value in jndi
     * @return encoded value for password, else the original value
     */
    protected String encodeValueWhenNeeded(final String jndiKey, final String value) {
        if (value.startsWith(ENCODING_PREFIX) || containsPassword(jndiKey)) {
            return ENCODED_PASSWORD_VALUE;
        } else {
            return value;
        }
    }

    private boolean containsPassword(final String jndiKey) {
        String cleanKey = cleanKey(jndiKey);
        for (String key : PASSWORD_KEYS) {
            if (StringUtils.contains(cleanKey, key)) {
                return true;
            }
        }
        return false;
    }

    private String cleanKey(final String jndiKey) {
        final StringBuilder builder = new StringBuilder();
        for (char c : jndiKey.toLowerCase(Locale.ENGLISH).toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
