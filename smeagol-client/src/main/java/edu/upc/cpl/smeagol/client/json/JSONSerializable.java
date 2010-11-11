package edu.upc.cpl.smeagol.client.json;

import java.util.Collection;

import org.json.JSONException;
import org.json.JSONString;

/**
 * Interface implemented by classes which can be serialized to, and parsed from
 * JSON strings.
 * 
 * @author angel
 * 
 * @param <T>
 *            the type of the class implementing the interface
 */
public interface JSONSerializable<T extends JSONString> {

	/**
	 * Serializes the collection to its JSON representation.
	 * 
	 * @param col
	 * @return
	 */
	public String toJSONArray(Collection<T> col);

	/**
	 * Returns a new object built from its JSON serialization.
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 *             if the string is not a valid representation of an object of
	 *             type <code>T</code>.
	 */
	public T fromJSONString(String json) throws JSONException;

	/**
	 * Builds a new collection of objects, parsing a JSON string which is the
	 * representation of an array of objects of type <code>T</code>.
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public Collection<T> fromJSONArray(String json) throws JSONException;

}
