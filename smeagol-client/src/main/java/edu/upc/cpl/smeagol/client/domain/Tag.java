package edu.upc.cpl.smeagol.client.domain;

import java.util.Collection;
import java.util.TreeSet;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * Tags are used to add semantic information to resources, events and bookings.
 * 
 * @author angel
 * 
 */
public class Tag implements Comparable<Tag> {
	protected Logger logger = Logger.getLogger(getClass());

	/**
	 * Maximum length for Tag id.
	 */
	public static final int MAX_ID_LEN = 64;

	/**
	 * Maximum length for Tag description.
	 */
	public static final int MAX_DESCRIPTION_LEN = 255;

	private String id = "";
	private String description = "";

	/**
	 * Create a tag without an empty description.
	 * 
	 * @param id
	 *            the tag's identifier. Should be a
	 */
	public Tag(String id) throws IllegalArgumentException {
		this.id = id;
	}

	/**
	 * Creates a new <code>Tag</code> with the supplied
	 * <code>id</id> and <code>description</code>.
	 * 
	 * @param id
	 *            An identifier for the new tag. Once the Tag is created, the id
	 *            is immutable.
	 * @param description
	 *            A description for the new tag.
	 */
	public Tag(String id, String description) {
		this.id = id;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Natural order between tags is defined by id, description.
	 */
	public int compareTo(Tag o) {
		return new CompareToBuilder().append(this.id, o.id).append(
				this.description, o.description).toComparison();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Tag t = (Tag) obj;

		return new EqualsBuilder().append(this.id, t.id).append(
				this.description, t.description).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(description)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(id).append(description)
				.toString();
	}

	public static Tag fromJsonString(String json) throws JSONException {
		JSONObject jo = new JSONObject(json);
		String id = jo.getString("id");
		String description = jo.getString("description");
		return new Tag(id, description);
	}

	public String toJsonString() throws JSONException {
		return new JSONStringer().object().key("id").value(id).key(
				"description").value(description).endObject().toString();
	}

	public static Collection<Tag> fromJsonArray(String json)
			throws JSONException {
		Collection<Tag> result = new TreeSet<Tag>();
		JSONArray ja = new JSONArray(json);
		for (int i = 0; i < ja.length(); i++) {
			JSONObject obj = ja.getJSONObject(i);
			String id = obj.getString("id");
			String desc = obj.getString("description");
			result.add(new Tag(id, desc));
		}
		return result;
	}

	public static String toJsonArray(Collection<Tag> tags) throws JSONException {
		JSONStringer js = new JSONStringer();
		js.array();
		for (Tag t : tags) {
			js.object().key("id").value(t.getId()).key("description").value(
					t.getDescription()).endObject();
		}
		js.endArray();
		return js.toString();
	}
}
