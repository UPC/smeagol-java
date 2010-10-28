package edu.upc.cpl.smeagol.client.domain;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONStringer;

public class Tag implements Comparable<Tag> {
	protected Logger	logger	= Logger.getLogger(getClass());

	private String		id;
	private String		description;

	/**
	 * Default constructor without arguments.
	 */
	public Tag() {
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

	public int compareTo(Tag o) {
		return new CompareToBuilder().append(this.id, o.id).append(this.description, o.description)
				.toComparison();
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

		return new EqualsBuilder().append(this.id, t.id).append(this.description, t.description)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(description).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(id).append(description).toString();
	}

	/**
	 * Returns a JSON representation of the tag. See {@link JSONStringer}.
	 * 
	 * @return A String containing a JSON-compliant representation of the tag.
	 *         If the tag cannot be converted to JSON, this method returns an
	 *         empty string.
	 */
	public String toJSON() {
		try {
			return new JSONStringer().object().key("id").value(id).key("description").value(
					description).endObject().toString();
		} catch (JSONException e) {
			logger.error(e.getLocalizedMessage());
			return "";
		}
	}

}




