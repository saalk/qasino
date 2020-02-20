package applyextra.commons.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;

/**
 * A utility class that assists in manipulating and handling Dates and Calendar operations.
  */
public final class DateUtils {

    /**
     * Increments the day of the current date by the given amount of <code>days</code>.
     * @param days The amount of days that need to be added to the current date.
     * @return Date
     */
    public static Date addDays(int days) {
        return addDays(new Date(), days);
    }

    /**
     * Increments the day of the <code>date</code> by the given amount of <code>days</code>.
     * @param date
     * @param days
     * @return Date
     */
    public static Date addDays(Date date, int days) {
        DateTime dt = new DateTime(date);
        dt = dt.plusDays(days);
        return dt.toDate();
    }

    /**
     * Increments the year of the current date by the given amount of <code>years</code>.
     * @param years
     * @return Date
     */
    public static Date addYears(int years) {
        return addYears(new Date(), years);
    }

    /**
     * Increments the year of the <code>date</code> by the given amount of <code>years</code>.
     * @param date
     * @param years
     * @return Date
     */
    public static Date addYears(Date date, int years) {
        DateTime dt = new DateTime(date);
        dt = dt.plusYears(years);
        return dt.toDate();
    }

    /**
     * Tests whether the <code>date1</code> is before <code>date2</code>
     * @param date1
     * @param date2
     * @return <code>true</code> if date1 is before date2
     */
    public static boolean isBefore(Date date1, Date date2) {
        LocalDate ld1 = new LocalDate(date1);
        LocalDate ld2 = new LocalDate(date2);

        return ld1.isBefore(ld2);
    }

    /**
     * Tests whether the <code>date1</code> is after <code>date2</code>
     * @param date1
     * @param date2
     * @return <code>true</code> if date1 is after date2
     */
    public static boolean isAfter(Date date1, Date date2) {
        LocalDate ld1 = new LocalDate(date1);
        LocalDate ld2 = new LocalDate(date2);

        return ld1.isAfter(ld2);
    }

    /**
     * Tests whether date1 is equal to date2. This will only test the date field types and not the time.
     * @param date1
     * @param date2
     * @return true if equal, else false
     */
    public static boolean isDateEqual(Date date1, Date date2) {
        LocalDate ld1 = new LocalDate(date1);
        LocalDate ld2 = new LocalDate(date2);

        return ld1.equals(ld2);
    }

    /**
     * Creates a {@link Date} object from the given strDate. It uses the default format of dd/MM/yyyy.
     * For a different format, see {@link DateUtils#parseDate(String, String)}.
     * @param strDate
     * @return Date
     */
    public static Date parseDate(String strDate) {
        return parseDate(strDate, "dd/MM/yyyy");
    }

    /**
     * Creates a {@link Date} object from the given strDate using the given format.
     * @param strDate
     * @param format
     * @return Date
     */
    public static Date parseDate(String strDate, String format) {
        LocalDate localDate = LocalDate.parse(strDate, DateTimeFormat.forPattern(format));
        return localDate.toDate();
    }

}
