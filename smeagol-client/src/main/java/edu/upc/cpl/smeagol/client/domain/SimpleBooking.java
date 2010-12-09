package edu.upc.cpl.smeagol.client.domain;

import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
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

	private DateTime start;
	private DateTime end;

	public SimpleBooking() {
	}

	public SimpleBooking(Integer id, Interval interval) {
		this.setId(id);
		this.start = interval.getStart();
		this.end = interval.getEnd();
	}

	@Override
	public DateTime getStart() {
		return this.start;
	}

	@Override
	public DateTime getEnd() {
		return this.end;
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
		return new ToStringBuilder(this).appendSuper(super.toString()).append("start", getStart())
				.append("end", getEnd()).toString();
	}

}
