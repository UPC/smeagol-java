package edu.upc.cpl.smeagol.client.domain;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import edu.upc.cpl.smeagol.client.ical.DayOfWeek;
import edu.upc.cpl.smeagol.client.ical.Frequency;
import edu.upc.cpl.smeagol.json.DayOfWeekListConverter;
import edu.upc.cpl.smeagol.json.ShortSetConverter;

/**
 * This class defines a booking affected by a recurrence, as defined by RFC
 * 2445.
 * <p>
 * Please keep in mind that Sméagol Server API v2.0 implements only a subset of
 * this standard. Please refer to <a
 * href="http://tools.ietf.org/html/rfc2445">RFC 2445</a>, section 4.3.10, to
 * get a detailed description of valid recurrence attribute values.
 * 
 * @author angel
 * 
 */
public class RecurringBooking extends Booking {

	private static transient Gson gson = new Gson();

	/**
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
		gson = gb.create();
	}

	private Set<DayOfWeek> by_day;
	private Set<Short> by_day_month; // valid values: [-1 .. -31, 1 .. 31]
	private Set<Short> by_hour; // valid values: [0 .. 23]
	private Set<Short> by_minute; // valid values: [0 .. 59]
	private Set<Short> by_month; // valid values: [1 .. 12]
	private DateTime dtend;
	private DateTime dtstart = new DateTime();
	private Duration duration;
	private Frequency frequency;
	private Short interval = 1;
	private DateTime until;

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
	 *            set of integer values in the ranges [-1 .. 31] or [1 .. 31].
	 */
	public void setByDayOfMonth(Set<Short> byDayOfMonth) {
		this.by_day_month = byDayOfMonth;
	}

	public Set<Short> getByHour() {
		return by_hour;
	}

	/**
	 * Set the hours of the day which are affected by the recurrence.
	 * 
	 * @param byHour
	 *            set of integer values in the range [0 .. 23]
	 */
	public void setByHour(Set<Short> byHour) {
		this.by_hour = byHour;
	}

	public Set<Short> getByMinute() {
		return by_minute;
	}

	/**
	 * Set the minutes of the hour which are affected by the recurrence.
	 * 
	 * @param byMinute
	 *            set of integer values in the range [0 .. 59]
	 */
	public void setByMinute(Set<Short> byMinute) {
		this.by_minute = byMinute;
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

	public DateTime getDtEnd() {
		return dtend;
	}

	public void setDtEnd(DateTime dtEnd) {
		this.dtend = dtEnd;
	}

	public DateTime getDtStart() {
		return dtstart;
	}

	public void setDtStart(DateTime dtStart) {
		this.dtstart = dtStart;
	}

	public Duration getDuration() {
		return duration;
	}

	/**
	 * Sets the duration of the booking.
	 * 
	 * @param duration
	 *            see {@link org.joda.time.Duration}
	 */
	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public Frequency getFrequency() {
		return frequency;
	}

	/**
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
	 * Set how often the recurrence rule repeats.
	 * 
	 * @param interval
	 */
	public void setInterval(Short interval) {
		this.interval = interval;
	}

	public DateTime getUntil() {
		return until;
	}

	/**
	 * Set the date/time when the recurrence ends. May be set to
	 * <code>null</code> to define an infinite recurrence.
	 * 
	 * @param until
	 */
	public void setUntil(DateTime until) {
		this.until = until;
	}

	@Override
	public DateTime getStart() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DateTime getEnd() {
		// TODO Auto-generated method stub
		return null;
	}

	public String serialize() {
		return gson.toJson(this);
	}

	public static String serialize(Collection<RecurringBooking> c) {
		return gson.toJson(c);
	}

	public static RecurringBooking deserialize(String json) {
		return gson.fromJson(json, RecurringBooking.class);
	}

	public static Collection<RecurringBooking> deserializeCollection(String json) {
		Type collectionType = new TypeToken<Collection<RecurringBooking>>() {
		}.getType();
		return gson.fromJson(json, collectionType);
	}

}
