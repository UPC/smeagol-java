package edu.upc.cpl.smeagol.json;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.upc.cpl.smeagol.client.ical.DayOfWeek;

/**
 * This class is used internally by the Sméagol client and should not be used
 * directly in your programs.
 * <p>
 * A custom serializer/deserializer for type
 * <code>List&lt;DayOfWeek&gt;</code>. To be used by <code>GsonBuilder</code>s.
 * 
 * This class is needed because default serialization provided by the Gson
 * library does not conform to Sméagol Server API v2.0.
 * 
 * @author angel
 * 
 */
public class DayOfWeekListConverter implements JsonSerializer<Set<DayOfWeek>>, JsonDeserializer<Set<DayOfWeek>> {

	public JsonElement serialize(Set<DayOfWeek> src, Type typeOfSrc, JsonSerializationContext context) {

		if (CollectionUtils.isEmpty(src)) {
			return null;
		}

		Set<String> days = new TreeSet<String>();

		for (DayOfWeek d : src) {
			days.add(d.getAsString());
		}

		return new JsonPrimitive(StringUtils.join(days, ","));
	}

	public Set<DayOfWeek> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		String jsonAsString = json.getAsString();
		if (StringUtils.isEmpty(jsonAsString)) {
			return null;
		}

		Set<DayOfWeek> result = new TreeSet<DayOfWeek>();
		String[] labels = StringUtils.split(jsonAsString, ",");

		for (String l : labels) {
			try {
				DayOfWeek d = new DayOfWeek(l.trim());
				result.add(d);
			} catch (IllegalArgumentException e) {
				throw new JsonParseException("Invalid DayOfWeek found on deserialization of " + json.getAsString());
			}
		}

		return result;
	}

}
