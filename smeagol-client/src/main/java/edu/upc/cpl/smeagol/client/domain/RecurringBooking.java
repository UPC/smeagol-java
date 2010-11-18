package edu.upc.cpl.smeagol.client.domain;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import javax.xml.datatype.Duration;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import edu.upc.cpl.smeagol.client.ical.DayOfWeek;
import edu.upc.cpl.smeagol.client.ical.Frequency;
import edu.upc.cpl.smeagol.json.DayOfWeekListConverter;
import edu.upc.cpl.smeagol.json.ShortListConverter;

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
		gb.registerTypeAdapter(listOfShortType, new ShortListConverter());
		gson = gb.create();
	}

	private List<DayOfWeek> by_day;
	private List<Short> by_day_month; // valid values: [-1 .. -31, 1 .. 31]
	private List<Short> by_hour; // valid values: [0 .. 23]
	private List<Short> by_minute; // valid values: [0 .. 59]
	private List<String> by_month; // valid values: [1 .. 12]
	private DateTime dtEnd;
	private DateTime dtStart;
	private Duration duration;
	private Frequency frequency;
	private Interval interval; // TODO: is it an Interval?
	private DateTime until;

	public List<DayOfWeek> getBy_day() {
		return by_day;
	}

	public void setBy_day(List<DayOfWeek> by_day) {
		this.by_day = by_day;
	}

	public List<Short> getBy_day_month() {
		return by_day_month;
	}

	public void setBy_day_month(List<Short> by_day_month) {
		this.by_day_month = by_day_month;
	}

	public List<Short> getBy_hour() {
		return by_hour;
	}

	public void setBy_hour(List<Short> by_hour) {
		this.by_hour = by_hour;
	}

	public List<Short> getBy_minute() {
		return by_minute;
	}

	public void setBy_minute(List<Short> by_minute) {
		this.by_minute = by_minute;
	}

	public List<String> getBy_month() {
		return by_month;
	}

	public void setBy_month(List<String> by_month) {
		this.by_month = by_month;
	}

	public DateTime getDtEnd() {
		return dtEnd;
	}

	public void setDtEnd(DateTime dtEnd) {
		this.dtEnd = dtEnd;
	}

	public DateTime getDtStart() {
		return dtStart;
	}

	public void setDtStart(DateTime dtStart) {
		this.dtStart = dtStart;
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

	public Interval getInterval() {
		return interval;
	}

	public void setInterval(Interval interval) {
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
