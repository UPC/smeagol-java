package edu.upc.cpl.smeagol.client.domain;

import java.io.Serializable;
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
import com.google.gson.reflect.TypeToken;

/**
 * Resources represent anything which can be booked, such as an object or
 * service. Resources have an <em>id</em> (automatically generated by the
 * server), a mandatory, unique, non-blank "description" and, possibly, some
 * related "info".
 * 
 * @author angel
 * 
 */
public class Resource implements Serializable, Comparable<Resource> {

	/**
	 * Required by the {@link Serializable} interface.
	 */
	private static final long serialVersionUID = -2987432624616511674L;

	@SuppressWarnings("unused")
	private static transient Logger logger = Logger.getLogger(Resource.class);
	private static transient Gson gson = new Gson();

	/**
	 * Maximum length for resource descriptions = {@value}
	 */
	public transient static final int DESCRIPTION_MAX_LEN = 128;

	/**
	 * Maximum length for resource info = {@value}
	 */
	public transient static final int INFO_MAX_LEN = 256;

	private Long id;
	private String description;
	private String info;

	/**
	 * Check if a string is a valid resource description.
	 * 
	 * @param candidate
	 *            the string to validate
	 * @return {@code true} if {@code candidate} is a valid resource
	 *         description. {@code false} otherwise.
	 * @see Resource#DESCRIPTION_MAX_LEN
	 */
	public static boolean validateDescription(String candidate) {
		return (candidate != null && StringUtils.isNotBlank(candidate) && GenericValidator.maxLength(candidate,
				DESCRIPTION_MAX_LEN));
	}

	/**
	 * Check if a string is a valid resource info.
	 * 
	 * @param candidate
	 * @return {@code true} if {@code candidate} is a valid string
	 * @see Resource#INFO_MAX_LEN
	 */
	public static boolean validateInfo(String candidate) {
		return (candidate == null || GenericValidator.maxLength(candidate, INFO_MAX_LEN));
	}

	/**
	 * Resource constructor.
	 * 
	 * @param description
	 * @throws IllegalArgumentException
	 *             if the provided description is not a valid resource
	 *             description
	 */
	public Resource(String description) {
		setDescription(description);
	}

	/**
	 * Resource constructor.
	 * 
	 * @param description
	 * @param info
	 * @throws IllegalArgumentException
	 *             if the provided description is <code>null</code> or an empty
	 *             or blank string.
	 */
	public Resource(String description, String info) {
		setDescription(description);
		setInfo(info);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	/**
	 * Set the description for the resource.
	 * 
	 * @param description
	 *            The description. It must be a non-empty, non-blank string.
	 * @throws IllegalArgumentException
	 *             if the description is not a valid resource description
	 */
	public void setDescription(String description) {
		if (!validateDescription(description)) {
			throw new IllegalArgumentException("invalid resource description");
		}
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setInfo(String info) {
		if (!validateInfo(info)) {
			throw new IllegalArgumentException("invalid resource info");
		}
		this.info = info;
	}

	public String getInfo() {
		return info;
	}

	public int compareTo(Resource other) {
		if (this == other) {
			return 0;
		}
		return new CompareToBuilder().append(this.description, other.description).toComparison();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Resource other = (Resource) obj;

		return new EqualsBuilder().append(this.id, other.id).append(this.description, other.description)
				.append(this.info, other.info).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("description", description).append("info", info)
				.toString();
	}

	public String serialize() {
		return gson.toJson(this);
	}

	public static String serialize(Collection<Resource> resources) {
		return gson.toJson(resources);
	}

	public static Resource deserialize(String json) {
		return gson.fromJson(json, Resource.class);
	}

	public static Collection<Resource> deserializeCollection(String json) {
		Type collectionType = new TypeToken<Collection<Resource>>() {
		}.getType();
		return gson.fromJson(json, collectionType);
	}

}
