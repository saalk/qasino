package applyextra.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * It makes possible to read the jndi values from a properties file io real JNDI
 */
public class PropertyFileJNDIUtilImpl extends AbstractJndiUtil implements JNDIUtil {

    private static final String DEFAULT_FILE_NAME = "/jndi.properties";
    private static final Logger LOG = LoggerFactory.getLogger(JNDIUtilImpl.class);

    private final Properties props = new Properties();

    /**
     * Load the properties file during initialization
     */
    public PropertyFileJNDIUtilImpl() {
        this(DEFAULT_FILE_NAME);
    }

    /**
     * Load the properties file during initialization
     *
     * @param propsfile properties file to read the values from
     */
    public PropertyFileJNDIUtilImpl(final String propsfile) {
        readProps(propsfile);
    }

    /**
     * Load multiple properties files during initialization
     *
     * @param files List of files with properties to read the values from
     */
    public PropertyFileJNDIUtilImpl(final String[] files) {
        for (String fileName : files) {
            readProps(fileName);
        }
    }

    private void readProps(final String fileName) {
        LOG.debug("Going to try to read properties from file {}", fileName);
        if (fileName != null) {
            try (InputStream fileInputStream = (new ClassPathResource(fileName)).getInputStream()) {
                this.props.load(fileInputStream);
                LOG.debug("successfully loaded properties from file {}", fileName);
            } catch (IOException e) {
                LOG.error("Can\'t read props", e);
            }
        } else {
            LOG.error("Given fileName is null");
        }
    }

    @Override
    protected String getValue(final String jndiKey) {
        String result = null;

        final Object foundObject = props.getProperty(jndiKey);
        if (foundObject != null) {
            result = foundObject.toString().trim();

            if (LOG.isDebugEnabled()) {
                LOG.debug("getValue with key {} gave result {}", jndiKey, encodeValueWhenNeeded(jndiKey, result));
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValueFromCell(final String jndiKey) {
        return getValue("cell/legacyRoot/" + jndiKey);
    }
}