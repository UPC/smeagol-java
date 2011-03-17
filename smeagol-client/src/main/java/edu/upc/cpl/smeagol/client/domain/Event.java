package edu.upc.cpl.smeagol.client.domain;

import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import edu.upc.cpl.smeagol.json.DateTimeConverter;

/**
 * Sméagol events.
 * <p>
 * In <em>real life</em>, events are the reason why people book resources.
 * <p>
 * In a Sméagol server, bookings are always related to one single event. This is
 * a <em>one-to-many</em> relationship (one event is related to zero or more
 * bookings).
 * <p>
 * Examples of events could be <em>Smeagol developers online meeting</em>,
 * <em>Perl conference</em>, etc.
 * 
 * @author angel
 * 
 */
public class Event implements Comparable<Event> {
	@SuppressWarnings("unused")
	private static transient Logger logger = Logger.getLogger(Event.class);
	private static transient Gson gson = new Gson();

	/**
	 * provide custom serializers/deserializers for several attributes
	 */
	static {
		GsonBuilder gb = new GsonBuilder();

		gb.registerTypeAdapter(DateTime.class, new DateTimeConverter());
		gson = gb.create();
	}

	/**
	 * Maximum length for the "description" field
	 * <p>
	 * TODO: check the maximum value allowed by the server
	 */
	public static transient final int MAX_DESCRIPTION_LEN = 50;

	/**
	 * Maximum length for the "info" length
	 * <p>
	 * TODO: check the current maximum value allowed by the server
	 */
	public static transient int MAX_INFO_LEN = 20;

	private Long id;
	private String description;
	private String info;
	private DateTime starts;
	private DateTime ends;

	public Event() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * Set the interval at which the event occurs.
	 * 
	 * @param interval
	 *            the interval
	 */
	public void setInterval(Interval interval) {
		if (interval != null) {
			this.starts = interval.getStart();
			this.ends = interval.getEnd();
		}
	}

	/**
	 * Get the interval at which the event occurs.
	 * 
	 * @return the interval
	 */
	public Interval getInterval() {
		return new Interval(starts, ends);
	}

	public int compareTo(Event other) {
		return new CompareToBuilder().append(this.description, other.description).toComparison();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}
		Event other = (Event) obj;

		return new EqualsBuilder().append(this.id, other.id).append(this.description, other.description).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("description", description).append("info", info)
				.toString();
	}

	public String serialize() {
		return gson.toJson(this);
	}

	public static String serialize(Collection<Event> events) {
		return gson.toJson(events);
	}

	public static Event deserialize(String json) throws JsonParseException {
		return gson.fromJson(json, Event.class);
	}

	public static Collection<Event> deserializeCollection(String json) throws JsonParseException {
		Type collectionType = new TypeToken<Collection<Event>>() {
		}.getType();
		return gson.fromJson(json, collectionType);
	}

}
