package applyextra.core.util;



import javax.annotation.Resource;

/**
 * This 'JNDI' implementation is based on Typesafe Config.
 * <p/>
 * See:  https://github.com/typesafehub/config
 * <p/>
 * It reads the application.conf config file on your classpath
 */
public class ConfigUtilImpl extends AbstractJndiUtil implements JNDIUtil {
    /**
     * The application must supply config spring bean.
     * This can be as simple as 'ConfigFactory.load()', that parses the 'application.conf'
     * file on the classpath.
     */
    @Resource
    private Config config;

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValue(final String jndiName) {
        String result;
        try {
            result = config.getString(convertJndiName(jndiName));
        } catch (ConfigException.Missing e) {
            result = null;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValueFromCell(final String jndiKey) {
        String result;
        try {
            result = config.getString(convertJndiName("cell/legacyRoot/" + jndiKey));
        } catch (ConfigException.Missing e) {
            result = null;
        }
        return result;
    }

    private String convertJndiName(final String jndiName) {
        return jndiName.replace('/', '.');
    }
}
