package edu.upc.cpl.smeagol.client.domain;

import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * Tags are used to add semantic information to resources, events and bookings.
 * 
 * @author angel
 * 
 */
public class Tag implements Comparable<Tag> {

	@SuppressWarnings("unused")
	private transient Logger logger = Logger.getLogger(getClass());
	private transient static Gson gson = new Gson();

	public transient static final String ID_ATTR_NAME = "id";
	public transient static final String DESCRIPTION_ATTR_NAME = "description";

	/**
	 * Maximum length for Tag id.
	 */
	public transient static final int MAX_ID_LEN = 64;

	/**
	 * Maximum length for Tag description.
	 */
	public transient static final int MAX_DESCRIPTION_LEN = 255;

	private String id;
	private String description;

	public Tag() {
	}

	/**
	 * Create a tag without an empty description.
	 * 
	 * @param id
	 *            the tag's identifier, not null, not empty.
	 * @throws IllegalArgumentException
	 *             if the provided id is null or an empty string ("").
	 */
	public Tag(String id) throws IllegalArgumentException {
		if (StringUtils.isEmpty(id)) {
			throw new IllegalArgumentException();
		}
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
	public Tag(String id, String description) throws IllegalArgumentException {
		if (StringUtils.isEmpty(id)) {
			throw new IllegalArgumentException();
		}
		this.id = id;
		this.description = description;
	}

	public void setId(String id) {
		this.id = id;
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
		return new CompareToBuilder().append(this.id, o.id).append(this.description, o.description).toComparison();
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

		return new EqualsBuilder().append(this.id, t.id).append(this.description, t.description).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(description).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("description", description).toString();
	}

	public String serialize() {
		return gson.toJson(this);
	}

	public static String serialize(Collection<Tag> c) {
		return gson.toJson(c);
	}

	public static Tag deserialize(String json) throws JsonParseException {
		return gson.fromJson(json, Tag.class);
	}

	public static Collection<Tag> deserializeCollection(String json) throws JsonParseException {
		Type tagCollection = new TypeToken<Collection<Tag>>() {
		}.getType();
		return gson.fromJson(json, tagCollection);
	}

}
