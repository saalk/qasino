package applyextra.core.util;


/**
 * Interface for the JNDIUtil class
 */
public interface JNDIUtil {

    /**
     * Indicates that the JNDI-parameter is optional.
     */
    boolean IS_OPTIONAL = false;


    /**
     * Indicates that the JNDI-parameter is mandatory.
     */
    boolean IS_MANDATORY = true;


    /**
     * JNDI-name for approved sites
     *
     * @since 0.4.22
     */
    String APPROVED_SITES = "param/riaf-additional-approved-sites";

    /**
     * JNDI-name indicating the environment for the generic logger
     *
     * @since 0.4.23
     */
    String GENERIC_LOG_DTAP_ENVIRONMENT = "param/generieke-logfile-adapter-omgeving";


    /**
     * JNDI-name for overwriting the default use of HTTPOnly on development environments
     *
     * @since 0.6.0
     */
    String COOKIE_HTTPONLY_OVERWRITE = "param/fordevelopment-overwrite-cookie-httponly";

    /**
     * JNDI-name for overwriting the default use of Secure Cookies on development environments
     *
     * @since 1.1.7
     */
    String DEFAULT_COOKIE_SECURE = "param/riaf-cookie-secure";

    /**
     * Reads a mandatory value from JNDI. Throws a RuntimeException in case
     * the setting is missing.
     *
     * @param jndiKey the JNDI Parameter-name to search for.
     * @return the JNDI parameter-value.
     */
    String getJndiValue(final String jndiKey);

    Long getJndiLongValue(final String jndiName, final boolean isMandatory);


    Integer getJndiIntValue(final String jndiName, final boolean isMandatory);


    Double getJndiDoubleValue(final String jndiKey, final boolean isMandatory);


    Boolean getJndiBooleanValue(final String jndiKey, final boolean isMandatory);


    String getJndiValue(final String jndiKey, final boolean isMandatory);


    String getJndiValueFromCell(final String jndiKey);


    String getJndiValueWithDefault(final String jndiKey, final String defaultValue);


    long getJndiValueWithDefault(final String jndiKey, final long defaultValue);


    double getJndiValueWithDefault(final String jndiKey, final double defaultValue);


    int getJndiValueWithDefault(final String jndiKey, final int defaultValue);



    boolean getJndiValueWithDefault(final String jndiKey, final boolean defaultValue);


    String getJndiPassword(final String jndiKey);
}
