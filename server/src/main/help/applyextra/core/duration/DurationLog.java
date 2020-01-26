package applyextra.core.duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * This class is a fast class to log duration information.
 * The code tries to fetch the method name from the stack.
 * THe code is very fast when debug is switch off, with debug on maybe on 
 * same jdk's faster options are available.
 *
 * This code has been tested under:
 * - SUN JDK 1.7
 * - Oracle JDK 1.7
 *
 * This code still contains code "that should work" in the following JDKs:
 * - SUN JDK 1.5
 * - SUN JDK 1.6
 * - IBM JDK 1.5
 * - IBM JDK 1.6
 * However, starting from RIAF 4.0.0 we no longer actively test and support these versions.
 */
public final class DurationLog {

    private static final Logger LOG = LoggerFactory.getLogger(DurationLog.class);

    /** we do some stuff to detect an jvm */
    private static final String JAVA_VENDOR = System.getProperty("java.vendor");
    private static final String JAVA_VERSION = System.getProperty("java.version");
    private static final boolean SUN_JRE = JAVA_VENDOR != null && JAVA_VENDOR.toLowerCase(Locale.ENGLISH).startsWith("sun");
    private static final boolean ORACLE_JRE = JAVA_VENDOR != null && JAVA_VENDOR.toLowerCase(Locale.ENGLISH).startsWith("oracle");
    private static final boolean SUN_OR_ORACLE_JRE = SUN_JRE || ORACLE_JRE;
    private static final boolean SUN_OR_ORACLE_JRE_16 = SUN_OR_ORACLE_JRE && JAVA_VERSION != null && JAVA_VERSION.startsWith("1.6");
    private static final boolean SUN_OR_ORACLE_JRE_17 = SUN_OR_ORACLE_JRE && JAVA_VERSION != null && JAVA_VERSION.startsWith("1.7");
    private static final boolean SUN_OR_ORACLE_JRE_18 = SUN_OR_ORACLE_JRE && JAVA_VERSION != null && JAVA_VERSION.startsWith("1.8");
    private static final int ROOT = (SUN_OR_ORACLE_JRE_16 || SUN_OR_ORACLE_JRE_17 || SUN_OR_ORACLE_JRE_18) ? 2 : 3;

    /**
     * Private constructor since this class is not meant for constructing
     */
    private DurationLog() {
        //no logic
    }

    /**
     * This method is called before the function from which we have to measure time
     * @param function the function name to take duration information from
     * @return duration record
     */
    public static DurationRecord logBefore(final String function) {
        final DurationRecord result;
        if (LOG.isDebugEnabled()) {
            final StackTraceElement stackTraceElemtent = Thread.currentThread().getStackTrace()[ROOT];
            result = new DurationRecord(stackTraceElemtent.getClassName()
                    + "." + stackTraceElemtent.getMethodName()
                    + "." + function);
        } else {
            //if debug is not enabled we keep things ultra fast
            result = null;
        }
        return result;
    }

    /**
     * This method is called before the function from which we have to measure time
     * This function tries to find out itself the methodname and 
     * is fast under java 1.5.
     *
     * @return duration record
     */
    public static DurationRecord logBefore() {
        final DurationRecord result;
        if (LOG.isDebugEnabled()) {
            final StackTraceElement stackTraceElemtent = Thread.currentThread().getStackTrace()[ROOT];
            result = new DurationRecord(
                    stackTraceElemtent.getClassName() + "." + stackTraceElemtent.getMethodName());
        } else {
            //if debug is not enabled we keep things ultra fast
            result = null;
        }
        return result;
    }

    /**
     * This method is called after the function from which we have to measure time
     * @param record duration record
     */
    public static void logAfter(final DurationRecord record) {
        if (record != null) {
            record.stop();
        }
    }
}
