package edu.upc.cpl.smeagol.json;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * A customized serializer/deserializer for type
 * <code>List&lt;Short&gt;</code>. To be used by <code>GsonBuilder</code>s.
 * 
 * This class is needed because default serialization provided by the Gson
 * library does not conform to Sméagol Server API v2.0.
 * 
 * @author angel
 * 
 */public class ShortSetConverter implements JsonSerializer<Set<Short>>, JsonDeserializer<Set<Short>> {

	public JsonElement serialize(Set<Short> src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(StringUtils.join(src, ","));
	}

	public Set<Short> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		
		Set<Short> result = new TreeSet<Short>();
		String[] values = StringUtils.split(json.getAsString(), ",");
		
		for (String v : values) {
			try {
				result.add(new Short(v));
			} catch (NumberFormatException e) {
				throw new JsonParseException("Invalid Short value found on deserialization");
			}
		}
		
		return result;
	}

}
