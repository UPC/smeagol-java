package edu.upc.cpl.smeagol.client.domain;

import java.util.Collection;

import edu.upc.cpl.smeagol.client.domain.Tag;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.json.JSONException;
import org.json.JSONStringer;

public class Resource implements Comparable<Resource> {

	public static final int MAX_DESCRIPTION_LEN = 128;
	public static final int MAX_INFO_LEN = 255;

	private int id;
	private String description;
	private byte[] info;
	private Collection<Tag> tags;

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

	public void setInfo(byte[] info) {
		this.info = info;
	}

	public byte[] getInfo() {
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
		return new EqualsBuilder().append(this.id, other.id).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public String toJsonString() throws JSONException {
		JSONStringer js = new JSONStringer();
		js.object().key("id").value(id).key("description").value(description)
				.key("info");

		if (!tags.isEmpty()) {
			js.key("tags");
			js.array();
			for (Tag t : tags) {
				js.value(t);
			}
			js.endArray();
		}
		js.endObject();
		return js.toString();
	}

	public static Resource fromJsonString(String json) throws JSONException {
		// TODO
		return null;
	}

	public Collection<Resource> fromJsonArray(String json) throws JSONException {
		// TODO
		return null;
	}

	public static String toJsonArray(Collection<Resource> resources)
			throws JSONException {
		// TODO
		return null;
	}
}
