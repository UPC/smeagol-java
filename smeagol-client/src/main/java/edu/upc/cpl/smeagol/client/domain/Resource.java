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

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * Resources represent anything which can be booked, such as an object or
 * service. Resources have an "id", a "description" and, possibly, some related
 * "info". Resources may have a collection of <code>Tag</code>s.
 * 
 * @author angel
 * 
 */
public class Resource implements Comparable<Resource> {
	@SuppressWarnings("unused")
	private static transient Logger logger = Logger.getLogger(Resource.class);
	private static transient Gson gson = new Gson();

	public static transient final int MAX_DESCRIPTION_LEN = 128;
	public static transient final int MAX_INFO_LEN = 255;

	private int id;
	private String description;
	private String info;
	private Collection<Tag> tags = new ArrayList<Tag>();

	public Resource() {
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getInfo() {
		return info;
	}

	public void setTags(Collection<Tag> tags) {
		this.tags = tags;
	}

	public Collection<Tag> getTags() {
		return tags;
	}

	public int compareTo(Resource other) {
		if (this == other) {
			return 0;
		}
		return new CompareToBuilder().append(this.description,
				other.description).toComparison();
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

		boolean attrEquals = new EqualsBuilder().append(this.id, other.id)
				.append(this.description, other.description).append(this.info,
						other.info).isEquals();
		return attrEquals
				&& CollectionUtils.isEqualCollection(this.getTags(), other
						.getTags());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
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

	public static String serialize(Collection<Resource> resources) {
		return gson.toJson(resources);
	}

	public static Resource deserialize(String json) throws JsonParseException {
		return gson.fromJson(json, Resource.class);
	}

	public static Collection<Resource> deserializeCollection(String json)
			throws JsonParseException {
		Type collectionType = new TypeToken<Collection<Resource>>() {
		}.getType();
		return gson.fromJson(json, collectionType);
	}

}
