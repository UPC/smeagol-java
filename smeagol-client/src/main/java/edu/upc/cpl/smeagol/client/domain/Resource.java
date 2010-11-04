package edu.upc.cpl.smeagol.client.domain;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONStringer;

/**
 * Resources represent anything which can be booked, such as an object or
 * service. Resources have an "id", a "description" and, possibly, some related
 * "info". Resources may have a collection of <code>Tag</code>s.
 * 
 * @author angel
 * 
 */
public class Resource implements Comparable<Resource>, JSONString {

	private static Logger logger = Logger.getLogger(Resource.class);

	public static final int MAX_DESCRIPTION_LEN = 128;
	public static final int MAX_INFO_LEN = 255;

	private int id;
	private String description;
	private String info;
	private Collection<Tag> tags = new ArrayList<Tag>();

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

		boolean tagEquals = false;
		boolean attrEquals = new EqualsBuilder().append(this.id, other.id)
				.append(this.description, other.description)
				.append(this.info, other.info).isEquals();
		if (attrEquals) {
			// tag lists must contain exactly same elements
			tagEquals = this.getTags().equals(other.getTags())
					|| (this.getTags().containsAll(other.getTags()) && other
							.getTags().containsAll(this.getTags()));
		}
		return attrEquals && tagEquals;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id)
				.append("description", description).append("info", info)
				.append("tags", tags).toString();
	}

	public String toJSONString() {
		JSONStringer js = new JSONStringer();
		try {
			js.object().key("id").value(new Integer(id)).key("description")
					.value(description).key("info").value(info);
			// add Resource tags into JSON string
			js.key("tags");
			js.array();
			for (Tag t : tags) {
				js.value(t);
			}
			js.endArray();
			js.endObject();

		} catch (JSONException e) {
			// this should never happen
			logger.error(e.getLocalizedMessage());
		}

		return js.toString();
	}

	public static Resource fromJSONString(String json) throws JSONException {
		JSONObject jo = new JSONObject(json);
		Resource r = new Resource();
		r.setId(jo.getInt("id"));
		r.setDescription(jo.getString("description"));
		r.setInfo(jo.getString("info"));
		JSONArray jsonTagArray = jo.getJSONArray("tags");
		Collection<Tag> tags = Tag.fromJSONArray(jsonTagArray.toString());
		r.setTags(tags);
		return r;
	}

	public static Collection<Resource> fromJSONArray(String json)
			throws JSONException {
		Collection<Resource> result = new ArrayList<Resource>();
		JSONArray ja = new JSONArray(json);
		for (int i = 0; i < ja.length(); i++) {
			result.add(Resource.fromJSONString(ja.getJSONObject(i).toString()));
		}
		return result;
	}

	public static String toJSONArray(Collection<Resource> resources) {
		JSONArray ja = new JSONArray();
		for (Resource r : resources) {
			ja.put(r);
		}
		return ja.toString();
	}

}
