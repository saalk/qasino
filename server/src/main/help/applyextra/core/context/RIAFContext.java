package applyextra.core.context;

import com.ing.api.toolkit.trust.context.ChannelContext;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

/**
 * Interface for the RIAF Context.
 * 
 * The RIAF Context is stored by an interceptor on the thread and 
 * can be by RIAF for auditlog, security monitoring, etc
 */
public interface RIAFContext {

	/**
	 * Returns the aiaf-version from the pom.xml file.
	 * @return the riaf-version.
	 */
	String getRiafVersion();
	
	/**
	 * Returns the requestDetails;
	 * @return the requestDetails.
	 */
	RequestDetails getRequestDetails();
	
	/**
	 * Returns the boolean that indicates if the request context is valid
	 * @return true, if valid
	 */
	boolean isValid();

	/**
	 * Returns the locale of the end user
	 * @return the locale of the end user
	 */
	Locale getLocale();
	
	/**
	 * Formats a date to a localized string 
	 * @param date date to format
	 * @return localized date string
	 */
	String formatLocalizedDate(Date date);

	/**
	 * Formats a time to a localized string 
	 * @param time time to format
	 * @return localized time string
	 */
	String formatLocalizedTime(Date time);
	
	/**
	 * Formats a short time to a localized string 
	 * @param time time to format
	 * @return localized short time string
	 */
	String formatLocalizedShortTime(Date time);
	
	/**
	 * Formats a decimal to a localized string
	 * @param decimal decimal to format
	 * @return localized decimal string
	 */
	String formatLocalizedDecimal(Number decimal);
	
	/**
	 * Returns the context path of the user's request
	 * @return the context path
	 */
	String getContextPath();
	
	/**
	 * Returns decimal format pattern
	 * @return decimal format pattern
	 */
	String getLocalizedDecimalFormatPattern();
	
	/**
	 * Returns decimal format pattern
	 * @param numberAsString String representing a Number.
	 * @return The number parsed from the string.
	 * @throws ParseException if the string can not be parsed.
	 */
	Number parseStringToNumber(String numberAsString) throws ParseException;

	/**
	 * Returns decimal format pattern
	 * @return The character representing the decimal separator
	 * @throws ParseException When an exception occurs during parsing
	 */
	char getLocalizedDecimalSeparator() throws ParseException;

	/**
	 * Retrieves the channel context as provided from the trusted calling gateway.
	 *
	 * @return the ChannelContext
	 */
	Optional<ChannelContext> getChannelContext();

    /**
     * Invalidates the current context
     */
    void invalidate();
}