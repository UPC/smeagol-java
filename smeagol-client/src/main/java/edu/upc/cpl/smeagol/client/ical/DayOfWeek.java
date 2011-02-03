package edu.upc.cpl.smeagol.client.ical;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;

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

	private static Logger logger = Logger.getLogger(DayOfWeek.class);

	/**
	 * Value for representing Sundays
	 */
	public static DayOfWeek SUNDAY = new DayOfWeek("SU");
	/**
	 * Value for representing Mondays
	 */
	public static DayOfWeek MONDAY = new DayOfWeek("MO");
	/**
	 * Value for representing Thursdays
	 */
	public static DayOfWeek TUESDAY = new DayOfWeek("TU");
	/**
	 * Value for representing Wednesdays
	 */
	public static DayOfWeek WEDNESDAY = new DayOfWeek("WE");
	/**
	 * Value for representing Thursdays
	 */
	public static DayOfWeek THURSDAY = new DayOfWeek("TH");
	/**
	 * Value for representing Fridays
	 */
	public static DayOfWeek FRIDAY = new DayOfWeek("FR");
	/**
	 * Value for representing Saturdays
	 */
	public static DayOfWeek SATURDAY = new DayOfWeek("SA");

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
			logger.debug("DayOfWeek is empty");
			return false;
		}

		Pattern p = Pattern.compile("([+-]?[0-9]{0,2})?(MO|TU|WE|TH|FR|SA|SU)");
		Matcher m = p.matcher(dayOfWeek);

		if (!m.matches()) {
			logger.debug("DayOfWeek does not match regexp.");
			return false;
		}

		if (m.groupCount() == 2 && StringUtils.isNotEmpty(m.group(1))) {
			// tye day is in the form +NNday or -NNday
			String prefix = m.group(1);
			logger.debug("groupCount(): " + m.groupCount() + ", DayOfWeek[prefix: " + m.group(1) + ", suffix: "
					+ m.group(2) + "]");
			if (!GenericValidator.isShort(prefix) || !GenericValidator.isInRange(Short.parseShort(prefix), 1, 53)) {
				logger.debug("DayOfWeek prefix is not in a valid range.");
				return false;
			}
		}

		return true;
	}
}
