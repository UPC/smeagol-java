package edu.upc.cpl.smeagol.client.domain;

import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import edu.upc.cpl.smeagol.json.DateTimeConverter;

/**
 * The simplest possible booking is represented by a continuos time interval.
 * <p>
 * Internally, the class holds the representation of the interval.
 * 
 * @see org.joda.time.Interval
 * 
 * @author angel
 * 
 */
public class SimpleBooking extends Booking {
	@SuppressWarnings("unused")
	private static transient Logger logger = Logger.getLogger(Booking.class);
	private static transient Gson gson;

	/**
	 * provide custom serializers/deserializers for several attributes
	 */
	static {
		GsonBuilder gb = new GsonBuilder();

		gb.registerTypeAdapter(DateTime.class, new DateTimeConverter());
		gson = gb.create();
	}

	private DateTime dtstart;
	private DateTime dtend;

	public SimpleBooking() {
	}

	public SimpleBooking(Long idResource, Long idEvent, Interval interval) {
		this.setIdResource(idResource);
		this.setIdEvent(idEvent);
		this.dtstart = interval.getStart();
		this.dtend = interval.getEnd();
	}

	public SimpleBooking(Long idResource, Long idEvent, DateTime start, Duration duration) {
		this.setIdResource(idResource);
		this.setIdEvent(idEvent);
		this.dtstart = start;
		this.dtend = start.plus(duration);
	}

	@Override
	public DateTime getDtStart() {
		return this.dtstart;
	}

	@Override
	public DateTime getDtEnd() {
		return this.dtend;
	}

	public Duration getDuration() {
		return new Duration(dtstart, dtend);
	}

	public String serialize() {
		return gson.toJson(this);
	}

	public static String serialize(Collection<Booking> c) {
		return gson.toJson(c);
	}

	public static Booking deserialize(String json) {
		return gson.fromJson(json, SimpleBooking.class);
	}

	public static Collection<Booking> deserializeCollection(String json) {
		Type collectionType = new TypeToken<Collection<SimpleBooking>>() {
		}.getType();
		return gson.fromJson(json, collectionType);
	}

	@Override
	public int compareTo(Booking b) {
		return super.compareTo(b);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).appendSuper(super.toString()).append("start", getDtStart())
				.append("end", getDtEnd()).toString();
	}

}
