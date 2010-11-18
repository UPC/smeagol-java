package edu.upc.cpl.smeagol.client.domain;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.Duration;

import org.joda.time.DateTime;

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
	private Set<String> by_month; // valid values: [1 .. 12]
	private DateTime dtend;
	private DateTime dtstart;
	private Duration duration;
	private Frequency frequency;
	private Short interval;
	private DateTime until;

	public Set<DayOfWeek> getByDay() {
		return by_day;
	}

	/**
	 * Set the days of the week which are affected by the recurrence. Valid
	 * values are defined by the {@link DayOfWeek} enumeration.
	 * 
	 * @param byDay
	 *            a list of <code>DayOfWeek</code> elements
	 */
	public void setByDay(Set<DayOfWeek> byDay) {
		this.by_day = byDay;
	}

	public Set<Short> getByDayOfMonth() {
		return by_day_month;
	}

	/**
	 * Set the days of the month which are affected by the recurrence. Valid
	 * values are integers in the ranges [-1 .. -31] or [1 .. 31].
	 * <p>
	 * Negative values start counting backwards from the last day of the month
	 * (i.e., "1" means "first day of the month", whereas "-1" means
	 * "last day of the month").
	 * 
	 * @param by_day_month
	 */
	public void setByDayOfMonth(Set<Short> by_day_month) {
		this.by_day_month = by_day_month;
	}

	public Set<Short> getByHour() {
		return by_hour;
	}

	public void setByHour(Set<Short> byHour) {
		this.by_hour = byHour;
	}

	public Set<Short> getByMinute() {
		return by_minute;
	}

	public void setByMinute(Set<Short> byMinute) {
		this.by_minute = byMinute;
	}

	public Set<String> getByMonth() {
		return by_month;
	}

	public void setByMonth(Set<String> byMonth) {
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

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public Frequency getFrequency() {
		return frequency;
	}

	public void setFrequency(Frequency frequency) {
		this.frequency = frequency;
	}

	public Short getInterval() {
		return interval;
	}

	public void setInterval(Short interval) {
		this.interval = interval;
	}

	public DateTime getUntil() {
		return until;
	}

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
