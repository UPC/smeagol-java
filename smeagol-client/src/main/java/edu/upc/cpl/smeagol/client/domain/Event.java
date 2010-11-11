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
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONStringer;

/**
 * Events are objects which can hold a number of Bookings.
 * 
 * @author angel
 * 
 */
public class Event implements Comparable<Event>, JSONString {

	private Logger logger = Logger.getLogger(getClass());

	/**
	 * Maximum length for the "description" field
	 */
	public static final int MAX_DESCRIPTION_LEN = 50;

	/**
	 * Maximum length for the "info" length
	 */
	public static int MAX_INFO_LEN = 20;

	private int id;
	private String description = "";
	private String info = "";
	private Collection<Tag> tags = new ArrayList<Tag>();

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

		boolean equalTags = false;
		boolean equalAttrs = new EqualsBuilder().append(this.id, other.id).append(this.description, other.description)
				.isEquals();
		if (equalAttrs) {
			// tag lists should be equal, element by element (ignoring order)
			equalTags = (this.tags == other.tags)
					|| (this.tags.containsAll(other.tags) && other.tags.containsAll(this.tags));
		}
		return equalAttrs && equalTags;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("description", description).append("info", info)
				.append("tags", tags).toString();
	}

	public String toJSONString() {
		JSONStringer js = new JSONStringer();
		try {
			js.object().key("id").value(new Integer(id)).key("description").value(description).key("info").value(info);
			js.key("tags");
			js.array();
			for (Tag t : tags) {
				js.value(t);
			}
			js.endArray();
			// TODO: Append bookings to result
			js.endObject();
		} catch (JSONException e) {
			// this should never happen
			logger.error(e.getLocalizedMessage());
		}
		return js.toString();
	}

	public static Event fromJSONString(String json) throws JSONException {
		JSONObject jo = new JSONObject(json);
		Event e = new Event();
		e.setId(jo.getInt("id"));
		e.setDescription(jo.getString("description"));
		e.setInfo(jo.getString("info"));
		JSONArray jsonTagArray = jo.getJSONArray("tags");
		Collection<Tag> tags = Tag.fromJSONArray(jsonTagArray.toString());
		e.setTags(tags);
		return e;
	}

	public static Collection<Event> fromJSONArray(String json) throws JSONException {
		JSONArray ja = new JSONArray(json);
		Collection<Event> result = new ArrayList<Event>();
		for (int i = 0; i < ja.length(); i++) {
			Event e = Event.fromJSONString(ja.get(i).toString());
			result.add(e);
		}
		return result;
	}

	public static String toJSONArray(Collection<Event> events) {
		JSONArray ja = new JSONArray();
		for (Event e : events) {
			ja.put(e);
		}
		return ja.toString();
	}

}
