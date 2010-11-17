package edu.upc.cpl.smeagol.client.domain;

import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
	private static transient Gson gson = new Gson();

	private Interval interval;

	public SimpleBooking() {
	}

	public SimpleBooking(Integer id, Interval interval) {
		this.setId(id);
		this.interval = interval;
	}

	@Override
	public DateTime getStart() {
		return this.interval.getStart();
	}

	@Override
	public DateTime getEnd() {
		return this.interval.getEnd();
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

}
