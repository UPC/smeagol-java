package edu.upc.cpl.smeagol.client.domain;

import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * Tags are used to add semantic information to resources, events and bookings.
 * <p>
 * Tags are identified by a case-insensitive id, and may have an optional
 * description.
 * 
 * @author angel
 */
public class Tag implements Comparable<Tag> {

	@SuppressWarnings("unused")
	private transient Logger logger = Logger.getLogger(getClass());
	private transient static Gson gson = new Gson();

	/**
	 * Minimum length for tag identifiers = {@value}
	 */
	public transient static final int ID_MIN_LEN = 3;

	/**
	 * Maximum length for tag identifiers = {@value}
	 */
	public transient static final int ID_MAX_LEN = 64;

	/**
	 * Maximum length for tag descriptions = {@value}
	 */
	public transient static final int DESCRIPTION_MAX_LEN = 256;

	private String id;
	private String description;

	/**
	 * Validate if a string is a valid Tag identifier.
	 * 
	 * @param candidate
	 *            the string to validate.
	 * @return {@code true} if {@code id} is a valid identifier. Otherwise
	 *         returns false.
	 */
	private static boolean isValidId(String candidate) {
		return (!StringUtils.isBlank(candidate) && GenericValidator.isInRange(candidate.length(), ID_MIN_LEN,
				ID_MAX_LEN));
	}

	private static boolean isValidDescription(String candidate) {
		return (candidate == null || GenericValidator.maxLength(candidate, DESCRIPTION_MAX_LEN));
	}

	/**
	 * Create a tag with an empty description.
	 * 
	 * @param id
	 *            the tag's identifier, not null, not empty.
	 * @throws IllegalArgumentException
	 *             if the provided id is null or an empty string ("").
	 */
	public Tag(String id) {
		setId(id);
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
	 * @throws IllegalArgumentException
	 *             if the provided arguments are not valid tag id and
	 *             description.
	 */
	public Tag(String id, String description) {
		setId(id);
		setDescription(description);
	}

	public void setId(String id) {
		if (!isValidId(id)) {
			throw new IllegalArgumentException("invalid tag id");
		}
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setDescription(String description) {
		if (!isValidDescription(description)) {
			throw new IllegalArgumentException("invalid tag description");
		}
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
