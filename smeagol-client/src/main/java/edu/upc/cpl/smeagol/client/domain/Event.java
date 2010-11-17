package edu.upc.cpl.smeagol.client.domain;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * Events are objects which can hold a number of Bookings.
 * 
 * @author angel
 * 
 */
public class Event implements Comparable<Event> {
	@SuppressWarnings("unused")
	private static transient Logger logger = Logger.getLogger(Event.class);
	private static transient Gson gson = new Gson();

	/**
	 * Maximum length for the "description" field
	 */
	public static transient final int MAX_DESCRIPTION_LEN = 50;

	/**
	 * Maximum length for the "info" length
	 */
	public static transient int MAX_INFO_LEN = 20;

	private Integer id;
	private String description;
	private String info;
	private Collection<Tag> tags = new ArrayList<Tag>();

	public Event() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public Collection<Tag> getTags() {
		return tags;
	}

	public void setTags(Collection<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * @return the DateTime at which the event starts, defined as the minimum
	 *         start DateTime of the bookings belonging to the event.
	 */
	public DateTime getStarts() {
		// TODO
		return null;
	}

	/**
	 * @return the date at which the event ends, defined as the maximum ending
	 *         DateTime of the bookings belonging to the event.
	 */
	public DateTime getEnds() {
		// TODO
		return null;
	}

	public int compareTo(Event other) {
		return new CompareToBuilder().append(this.description,
				other.description).toComparison();
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

		boolean equalAttrs = new EqualsBuilder().append(this.id, other.id)
				.append(this.description, other.description).isEquals();

		return equalAttrs
				&& CollectionUtils.isEqualCollection(this.tags, other.tags);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("description",
				description).append("info", info).append("tags", tags)
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

	public static Collection<Event> deserializeCollection(String json)
			throws JsonParseException {
		Type collectionType = new TypeToken<Collection<Event>>() {
		}.getType();
		return gson.fromJson(json, collectionType);
	}

}
