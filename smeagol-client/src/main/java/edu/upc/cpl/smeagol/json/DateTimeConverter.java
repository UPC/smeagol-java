package edu.upc.cpl.smeagol.json;

import java.lang.reflect.Type;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * This class is used internally by the Sméagol client and should not be used
 * directly in your programs.
 * <p>
 * A custom serializer/deserializer for {@code DateTime} objects. To be used by
 * {@code GsonBuilder}s.
 * <p>
 * This class is needed because default {@code DateTime} serialization provided
 * by the Gson library does not conform to Sméagol Server API v2.0.
 * 
 * @author angel
 * 
 */
public class DateTimeConverter implements JsonSerializer<DateTime>,
		JsonDeserializer<DateTime> {
	public JsonElement serialize(DateTime src, Type typeOfSrc,
			JsonSerializationContext context) {
		return new JsonPrimitive(ISODateTimeFormat.dateTimeNoMillis()
				.print(src));
	}

	public DateTime deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		return new DateTime(json.getAsJsonPrimitive().getAsString());
	}
}
