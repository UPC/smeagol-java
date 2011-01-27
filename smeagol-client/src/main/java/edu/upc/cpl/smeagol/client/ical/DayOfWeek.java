package edu.upc.cpl.smeagol.client.ical;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;

/**
 * Days of week, for recurrences. Valid days have the form "MO" (monday), "-1MO"
 * (the last monday), "3TH" (the third thursday), etc.
 * <p>
 * Several constants are provided for the most common values.
 * <p>
 * See RFC 5545 section 3.3.10 for further info.
 * 
 * @author angel
 * 
 */
public class DayOfWeek {

	/**
	 * Value for representing Sundays
	 */
	public static String SUNDAY = "SU";
	/**
	 * Value for representing Mondays
	 */
	public static String MONDAY = "MO";
	/**
	 * Value for representing Thursdays
	 */
	public static String TUESDAY = "TU";
	/**
	 * Value for representing Wednesdays
	 */
	public static String WEDNESDAY = "WE";
	/**
	 * Value for representing Thursdays
	 */
	public static String THURSDAY = "TH";
	/**
	 * Value for representing Fridays
	 */
	public static String FRIDAY = "FR";
	/**
	 * Value for representing Saturdays
	 */
	public static String SATURDAY = "SA";

	private String dayOfWeek;

	public DayOfWeek(String dayOfWeek) throws IllegalArgumentException {
		if (!validate(dayOfWeek)) {
			throw new IllegalArgumentException("invalid day of week");
		}
		this.dayOfWeek = dayOfWeek;
	}

	public String getAsString() {
		return this.dayOfWeek;
	}

	/**
	 * Check if string is a valid <code>DayOfWeek</code>
	 * 
	 * @param dayOfWeek
	 *            string to check
	 */
	public static boolean validate(String dayOfWeek) {
		if (StringUtils.isEmpty(dayOfWeek)) {
			return false;
		}

		Pattern p = Pattern.compile("([+-]?[0-9]{0,2})?(MO|TU|WE|TH|FR|SA|SU)");
		Matcher m = p.matcher(dayOfWeek);

		if (!m.matches()) {
			return false;
		}

		if (m.groupCount() == 2) {
			// tye day is in the form +NNday or -NNday
			String prefix = m.group(1);
			if (!GenericValidator.isShort(prefix) || !GenericValidator.isInRange(Short.parseShort(prefix), 1, 53)) {
				return false;
			}
		}

		return true;
	}
}
