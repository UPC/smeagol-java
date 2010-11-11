package edu.upc.cpl.smeagol.client.domain;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONString;

import edu.upc.cpl.smeagol.client.json.JSONSerializable;

/**
 * This is the base class of the Booking family. This class defines the minimum
 * behaviour to be implemented by a booking. It barely states that bookings have
 * an id, a natural order defined by some <code>start</code> and
 * <code>end</code> instants, and can be serialized to, and parsed from JSON
 * strings.
 * 
 * @author angel
 * 
 */
public abstract class Booking implements Comparable<Booking>, JSONSerializable<Booking>, JSONString {

	private static final Logger logger = Logger.getLogger(Booking.class);

	private Integer id;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public abstract DateTime getStart();

	public abstract DateTime getEnd();

	/**
	 * Defines natural order between bookings.
	 */
	public int compareTo(Booking b) {
		logger.debug("compareTo " + this.toString() + " versus " + b.toString());
		return new CompareToBuilder().append(this.getStart(), b.getStart()).append(this.getEnd(), b.getEnd())
				.toComparison();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("start", this.getStart()).append("end", this.getEnd()).toString();
	}

	public abstract String toJSONString();

	public abstract Booking fromJSONString(String json) throws JSONException;

	public Collection<Booking> fromJSONArray(String json) throws JSONException {
		Collection<Booking> result = new ArrayList<Booking>();
		JSONArray ja = new JSONArray(json);
		for (int i = 0; i < ja.length(); i++) {
			Booking b = this.fromJSONString(ja.getString(i).toString());
			result.add(b);
		}
		return result;
	};

	public String toJSONArray(Collection<Booking> bookings) {
		JSONArray ja = new JSONArray();
		for (Booking b : bookings) {
			ja.put(b);
		}
		return ja.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (this == o) {
			return true;
		}
		if (!(o instanceof SimpleBooking)) {
			return false;
		}
		SimpleBooking other = (SimpleBooking) o;
		return new EqualsBuilder().append(this.id, other.getId()).append(this.getStart(), other.getStart())
				.append(this.getEnd(), other.getEnd()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getId()).toHashCode();
	}

}
