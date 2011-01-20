package edu.upc.cpl.smeagol.client.domain;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.validator.GenericValidator;
import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import edu.upc.cpl.smeagol.client.ical.DayOfWeek;
import edu.upc.cpl.smeagol.client.ical.Frequency;
import edu.upc.cpl.smeagol.json.DateTimeConverter;
import edu.upc.cpl.smeagol.json.DayOfWeekListConverter;
import edu.upc.cpl.smeagol.json.ShortSetConverter;

/**
 * This class defines a booking affected by a recurrence, as defined by RFC
 * 5545.
 * <p>
 * Keep in mind that Sméagol Server API v2.0 implements <strong>only a
 * subset</strong> of this standard. Please refer to <a
 * href="http://tools.ietf.org/html/rfc5545">RFC 5545</a>, section 3.3.10, to
 * get a detailed description of valid recurrence attribute values.
 * <p>
 * TODO: Add details about which features of the RFC subset are implemented.
 * 
 * @author angel
 * 
 */
public class RecurrentBooking extends Booking {

	private static transient Gson gson = new Gson();

	/*
	 * provide custom serializers/deserializers for several attributes
	 */
	static {
		GsonBuilder gb = new GsonBuilder();

		Type listOfDayOfWeekType = new TypeToken<List<DayOfWeek>>() {
		}.getType();
		Type listOfShortType = new TypeToken<List<Short>>() {
		}.getType();
		gb.registerTypeAdapter(listOfDayOfWeekType, new DayOfWeekListConverter());
		gb.registerTypeAdapter(listOfShortType, new ShortSetConverter());
		gb.registerTypeAdapter(DateTime.class, new DateTimeConverter());
		gson = gb.create();
	}

	/**
	 * Default dtstart is "now"
	 */
	private DateTime dtstart = new DateTime();
	/**
	 * Default dtend is "dtstart + 1 hour"
	 */
	private DateTime dtend = dtstart.plusHours(1);
	private Frequency frequency;
	private Short interval = 1; // default repeat: "every 1"
	private DateTime until;
	private Set<DayOfWeek> by_day;
	private Set<Short> by_day_month; // valid values: [-1 .. -31, 1 .. 31]
	private Set<Short> by_month; // valid values: [1 .. 12]

	/**
	 * Default non-argument constructor. Protected because users should not be
	 * allowed to call it directly.
	 */
	protected RecurrentBooking() {
	}

	/**
	 * Returns a booking which repeats on a daily basis. For example, to define
	 * a booking for a resource identified by <code>resId</code> for an event
	 * identified by <code>evId</code>, every day from <code>8:00am</code> to
	 * <code>10:00am</code>, starting on <code>2010-02-01</code> during the next
	 * <code>3</code> monts:
	 * 
	 * <pre>
	 * DateTime start = new DateTime(2010, 2, 10, 8, 0, 0);
	 * DateTime end = start.plusHours(2);
	 * DateTime until = start.plusMonths(3);
	 * 
	 * RecurrentBooking booking = RecurrentBooking.createDailyRecurrence(resId, evId, start, end, 1, until);
	 * </pre>
	 * 
	 * @param idResource
	 *            identifier of the <code>Resource</code> to be booked. Not
	 *            null. The resource must exist.
	 * @param idEvent
	 *            identifier of the <code>Event</code> related to the booking.
	 *            Not null. The event must exist.
	 * @param start
	 *            the <code>DateTime</code> at which the booking starts every
	 *            day. Default is "now".
	 * @param end
	 *            the <code>DateTime</code> at which the booking ends every day.
	 *            Default is 1 hour after <code>start</code>.
	 * @param interval
	 *            the booking will repeat in steps of <code>interval</code>
	 *            days. Must be > 0 (otherwise
	 *            <code>IllegalArgumentException</code> will be thrown). Default
	 *            is 1 (every day).
	 * @param until
	 *            the <code>DateTime</code> at which the recurrence ends. If
	 *            null, the booking never ends. If not null, <code>until</code>
	 *            must be greater than <code>start</code> (otherwise, an
	 *            <code>IllegalArgumentException</code> will be thrown).
	 * @return a new RecurrentBooking for the specified daily recurrence.
	 * @throws IllegalArgumentException
	 */
	public static RecurrentBooking asDailyRecurrence(Long idResource, Long idEvent, DateTime start, DateTime end,
			Short interval, DateTime until) throws IllegalArgumentException {

		if (interval != null && interval.compareTo((short) 1) < 0) {
			throw new IllegalArgumentException("interval should be > 0");
		}

		if (until != null && until.isBefore(start)) {
			throw new IllegalArgumentException("until DateTime should be after start DateTime");
		}

		RecurrentBooking b = new RecurrentBooking();
		b.setDtStart(start);
		b.setDtEnd(end);
		b.setFrequency(Frequency.DAILY);
		b.setInterval(interval);
		b.setUntil(until);

		return b;
	}

	public static RecurrentBooking asWeeklyRecurrence(Long idResource, Long idEvent, DateTime start, DateTime end,
			Set<DayOfWeek> byDay, Short interval, DateTime until) {

		RecurrentBooking b = new RecurrentBooking();
		b.setDtStart(start);
		b.setDtEnd(end);
		b.setFrequency(Frequency.WEEKLY);
		b.setByDay(byDay);
		b.setInterval(interval);
		b.setUntil(until);

		return b;
	}

	public Set<DayOfWeek> getByDay() {
		return by_day;
	}

	/**
	 * Set the days of the week which are affected by the recurrence.
	 * 
	 * @param byDay
	 *            a list of {@link DayOfWeek} values
	 */
	public void setByDay(Set<DayOfWeek> byDay) {
		this.by_day = byDay;
	}

	public Set<Short> getByDayOfMonth() {
		return by_day_month;
	}

	/**
	 * Set the days of the month which are affected by the recurrence.
	 * <p>
	 * Negative values start counting backwards from the last day of the month
	 * (i.e., "1" means "first day of the month", whereas "-1" means
	 * "last day of the month").
	 * 
	 * @param byDayOfMonth
	 *            set of values in the ranges [-31 .. -1] or [1 .. 31].
	 * @throws IllegalArgumentException
	 *             if byDayOfMonth contains any illegal value
	 */
	public void setByDayOfMonth(Set<Short> byDayOfMonth) throws IllegalArgumentException {
		for (Short d : byDayOfMonth) {
			if (d == null || (!GenericValidator.isInRange(d, -31, -1) && !GenericValidator.isInRange(d, 1, 31))) {
				throw new IllegalArgumentException(
						"illegal day of month value: only values between ranges [-31 .. -1] or [1 .. 31] are valid");
			}
		}
		this.by_day_month = byDayOfMonth;
	}

	public Set<Short> getByMonth() {
		return by_month;
	}

	/**
	 * Set the months affected by the recurrence.
	 * 
	 * @param byMonth
	 *            set of integer values in the range [1 .. 12]
	 */
	public void setByMonth(Set<Short> byMonth) {
		this.by_month = byMonth;
	}

	public DateTime getDtStart() {
		return dtstart;
	}

	public void setDtStart(DateTime dtStart) {
		this.dtstart = (dtStart == null) ? new DateTime() : dtStart;
	}

	public DateTime getDtEnd() {
		return dtend;
	}

	public void setDtEnd(DateTime dtEnd) {
		this.dtend = (dtEnd == null) ? getDtStart().plusHours(1) : dtEnd;
	}

	public Frequency getFrequency() {
		return frequency;
	}

	/**
	 * The </code>Frequency</code> rule part identifies the type of recurrence
	 * rule. Valid values include DAILY, to specify repeating events based on an
	 * interval of a day or more; WEEKLY, to specify repeating events based on
	 * an interval of a week or more; MONTHLY, to specify repeating events based
	 * on an interval of a month or more; and YEARLY, to specify repeating
	 * events based on an interval of a year or more.
	 * 
	 * Sets the frequency of the booking. Its used in combination with
	 * <code>byDay</code>, <code>byDayOfMonth</code>, <code>byHour</code> or
	 * <code>byMinute</code> attributes.
	 * <p>
	 * For example, within a <code>frequency = Frequency.MONTHLY</code> booking,
	 * a <code>byDay = DayOfWeek.MONDAY</code> value represents <em>all Mondays
	 * within the month</em>.
	 * 
	 * @param see
	 *            {@see Frequency}
	 */
	public void setFrequency(Frequency frequency) {
		this.frequency = frequency;
	}

	public Short getInterval() {
		return interval;
	}

	/**
	 * The INTERVAL rule part contains a positive integer representing at which
	 * intervals the recurrence rule repeats. The default value is "1", every
	 * day for a DAILY rule, every week for a WEEKLY rule, every month for a
	 * MONTHLY rule, and every year for a YEARLY rule. For example, within a
	 * DAILY rule, a value of "8" means every eight days.
	 * 
	 * @param interval
	 *            the new interval value. Default is <code>1</code>.
	 */
	public void setInterval(Short interval) throws IllegalArgumentException {
		if (interval != null && interval.compareTo((short) 1) < 0) {
			throw new IllegalArgumentException("interval must be greater than 0");
		}
		this.interval = interval;
	}

	public DateTime getUntil() {
		return until;
	}

	/**
	 * The <code>UNTIL</code> rule part defines a DateTime value that bounds the
	 * recurrence rule in an inclusive manner. If the value specified by UNTIL
	 * is synchronized with the specified recurrence, this DateTime becomes the
	 * last instance of the recurrence.
	 * <p>
	 * If UNTIL is null, the recurrence is considered to repeat forever.
	 * 
	 * @param until
	 *            the <code>DateTime</code> where the recurrence ends.
	 */
	public void setUntil(DateTime until) throws IllegalArgumentException {
		if (until != null && until.isBefore(getDtStart())) {
			throw new IllegalArgumentException("until cannot be before dtStart");
		}
		this.until = until;
	}

	public String serialize() {
		return gson.toJson(this);
	}

	public static String serialize(Collection<RecurrentBooking> c) {
		return gson.toJson(c);
	}

	public static RecurrentBooking deserialize(String json) {
		return gson.fromJson(json, RecurrentBooking.class);
	}

	public static Collection<RecurrentBooking> deserializeCollection(String json) {
		Type collectionType = new TypeToken<Collection<RecurrentBooking>>() {
		}.getType();
		return gson.fromJson(json, collectionType);
	}

}
