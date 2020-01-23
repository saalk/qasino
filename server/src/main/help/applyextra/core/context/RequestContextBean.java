package applyextra.core.context;

import com.ing.api.toolkit.trust.context.ChannelContext;

import java.text.*;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

/**
 * This bean holds all data for the RequestContext. Since the bean is intended to be used
 * only for some very specific technical reasons, it is rather limited in possibilities.
 * Most values are immutable after construction (sessionId, partyId and timestamp). The
 * mutable booleans can only be changed in one direction (the bean can be be set to be
 * invalid, but can never be set to valid again and we can set an external cache invalidation
 * to be needed, but can not set it to be no longer needed once set).
 */
public final class RequestContextBean implements RIAFContext {

    private boolean valid = true;
    private SimpleDateFormat dateFormat = null;
    private SimpleDateFormat timeFormat = null;
    private SimpleDateFormat shortTimeFormat = null;
    private DecimalFormat decimalFormat = null;
    private Locale locale;
    private final String contextPath;
    private final RequestDetails requestDetails;
    private final String riafVersion;
    private final Optional<ChannelContext> channelContext;

    /**
     * Constructor of a RequestContextBean
     *
     * @param channelContext
     * @param requestDetails details of the request
     * @param riafVersion    versionnumber of the riaf
     * @param contextPath    the context path of the customer
     */
    public RequestContextBean(final ChannelContext channelContext,  final RequestDetails requestDetails, final String riafVersion,
                              final String contextPath) {
        this.channelContext = Optional.ofNullable(channelContext);
        this.riafVersion = riafVersion;
        this.requestDetails = requestDetails;
        this.contextPath = contextPath;
        this.locale = new Locale("nl", "NL");
    }

    /**
     * Returns the boolean that indicates if the request context is valid
     *
     * @return true, if valid else false
     */
    @Override
    public boolean isValid() {
        return valid;
    }

    /**
     * Sets the request context bean to invalid
     * It can never set to valid anymore
     */
    public void invalidate() {
        this.valid = false;
    }

    /**
     * Returns the locale of the user
     *
     * @return the locale of the user
     */
    @Override
    public Locale getLocale() {
        return locale;
    }

    /**
     * Formats a date to a localized string
     *
     * @param date date to format
     * @return localized date string
     */
    @Override
    public String formatLocalizedDate(final Date date) {
        return getLocalizedDateFormat().format(date);
    }

    /**
     * Formats a date to a localized string
     *
     * @param time the time to format
     * @return localized date string
     */
    @Override
    public String formatLocalizedTime(final Date time) {
        return getLocalizedTimeFormat().format(time);
    }

    /**
     * Formats a date to a localized string
     *
     * @param time the time to format
     * @return localized date string
     */
    @Override
    public String formatLocalizedShortTime(final Date time) {
        return getLocalizedShortTimeFormat().format(time);
    }

    /**
     * Formats a decimal to a localized string
     *
     * @param decimal decimal to format
     * @return localized decimal string
     */
    @Override
    public String formatLocalizedDecimal(final Number decimal) {
        return getLocalizedDecimalFormat().format(decimal);
    }

    /**
     * Returns the preferred decimal number formatter of the user.
     *
     * @return a DecimalFormat, based on the locale of the user
     */
    public DecimalFormat getLocalizedDecimalFormat() {
        if (decimalFormat == null) {
            //We don't use a pool here, since the request context is already per thread
            // and creating number formats is fast
            decimalFormat = createNewDecimalFormat();
        }
        return decimalFormat;
    }

    /**
     * Returns the preferred decimal number formatter pattern of the user.
     *
     * @return a DigitalFormatPattern
     */
    @Override
    public String getLocalizedDecimalFormatPattern() {
        return getLocalizedDecimalFormat().toLocalizedPattern();
    }

    /**
     * Returns decimal format pattern
     *
     * @param numberAsString String representing a Number
     * @return The number parsed from the string
     * @throws ParseException if the string can not be parsed.
     */
    @Override
    public Number parseStringToNumber(final String numberAsString) throws ParseException {
        return getLocalizedDecimalFormat().parse(numberAsString);
    }

    /**
     * Returns decimal format pattern
     *
     * @return The character representing the decimal separator
     * @throws ParseException When a unexpected parsing error occurs
     */
    @Override
    public char getLocalizedDecimalSeparator() throws ParseException {
        return getLocalizedDecimalFormat().getDecimalFormatSymbols().getDecimalSeparator();
    }

    /**
     * Returns the details of the request (various ip-addresses and hostnames).
     *
     * @return the requestDetails.
     */
    @Override
    public RequestDetails getRequestDetails() {
        return requestDetails;
    }

    /**
     * Returns the riaf version from the pom-file.
     *
     * @return the riafVersion.
     */
    @Override
    public String getRiafVersion() {
        return riafVersion;
    }

    /**
     * Returns the preferred date formatter of the user for formatting dates.
     * This method uses the locale and therefore returns a localized string.
     *
     * @return a localized preferred date formatter of the user for formatting times.
     */
    private DateFormat getLocalizedDateFormat() {
        if (dateFormat == null) {
            //We don't use a pool here, since the request context is already per thread
            // and creating simpledate formats is fast
            dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.SHORT, locale);
            String pattern = dateFormat.toLocalizedPattern();
            pattern = pattern.replaceAll("\\bd\\b", "dd");
            pattern = pattern.replaceAll("\\bM\\b", "MM");
            pattern = pattern.replaceAll("\\byy\\b", "yyyy");
            dateFormat.applyLocalizedPattern(pattern);
        }
        return dateFormat;
    }

    /**
     * Returns the preferred date formatter of the user (for times only).
     * This method uses the locale and therefore returns a localized string
     *
     * @return a localized preferred date formatter of the user for times.
     */
    private DateFormat getLocalizedTimeFormat() {
        if (timeFormat == null) {
            //We don't use a pool here, since the request context is already per thread
            // and creating simpledate formats is fast
            timeFormat = (SimpleDateFormat) SimpleDateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
        }
        return timeFormat;
    }

    /**
     * Returns the preferred date formatter of the user (for short times only, short times are without seconds).
     * This method uses the locale and therefore returns a localized string
     *
     * @return a localized preferred date formatter of the user for short times.
     */
    private DateFormat getLocalizedShortTimeFormat() {
        if (shortTimeFormat == null) {
            //We don't use a pool here, since the request context is already per thread
            // and creating simpledate formats is fast
            shortTimeFormat = (SimpleDateFormat) SimpleDateFormat.getTimeInstance(DateFormat.SHORT, locale);
        }
        return shortTimeFormat;
    }

    /**
     * Creates a decimal formatter, based on the locale of the user
     *
     * @return a DecimalFormat, based on the locale of the user.
     */
    private DecimalFormat createNewDecimalFormat() {
        final DecimalFormat result;
        final NumberFormat format = NumberFormat.getInstance(locale);
        if (format instanceof DecimalFormat) {
            result = (DecimalFormat) format;
        } else {
            final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(locale);
            result = new DecimalFormat("#,##0.00", decimalFormatSymbols);
        }
        result.setMinimumFractionDigits(2);
        result.setMaximumFractionDigits(2);
        result.setGroupingUsed(true);

        return result;
    }

    /**
     * Returns the context path of the user's request
     *
     * @return the context path
     */
    @Override
    public String getContextPath() {
        return contextPath;
    }

    /**
     * {@inheritDoc}
     */
    public Optional<ChannelContext> getChannelContext() {
        return channelContext;
    }

}