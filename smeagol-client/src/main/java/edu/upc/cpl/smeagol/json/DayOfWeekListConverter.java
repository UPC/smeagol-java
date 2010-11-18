package edu.upc.cpl.smeagol.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
 * A customized serializer/deserializer for type
 * <code>List&lt;DayOfWeek&gt;</code>. To be used by <code>GsonBuilder</code>s.
 * 
 * This class is needed because default serialization provided by the Gson
 * library does not conform to Sméagol Server API v2.0.
 * 
 * @author angel
 * 
 */
public class DayOfWeekListConverter implements JsonSerializer<List<DayOfWeek>>, JsonDeserializer<List<DayOfWeek>> {

	public JsonElement serialize(List<DayOfWeek> src, Type typeOfSrc, JsonSerializationContext context) {
		List<String> labels = new ArrayList<String>();
		for (DayOfWeek d : src) {
			labels.add(d.getLabel());
		}
		return new JsonPrimitive(StringUtils.join(labels, ","));
	}

	public List<DayOfWeek> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		List<DayOfWeek> result = new ArrayList<DayOfWeek>();
		String[] labels = StringUtils.split(json.getAsString(), ",");
		for (String l : labels) {
			try {
				DayOfWeek d = DayOfWeek.newFromLabel(l.trim());
				result.add(d);
			} catch (IllegalArgumentException e) {
				throw new JsonParseException("Invalid DayOfWeek found on deserialization of " + json.getAsString());
			}
		}
		return result;
	}

}
