package edu.upc.cpl.smeagol.client;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.core.MediaType;

import org.json.JSONException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import edu.upc.cpl.smeagol.client.domain.Tag;

/**
 * This class implements a basic Sméagol client conforming to server API version
 * 2.
 * 
 * @author angel
 * 
 */
public class SmeagolClient {

	private static final String TAG_PATH = "tag";
	private static final String RESOURCE_PATH = "resource";
	private static final String EVENT_PATH = "event";
	private static final String BOOKING_PATH = "booking";

	private Client client;

	private WebResource tag;
	private WebResource resource;
	private WebResource event;
	private WebResource booking;

	/**
	 * 
	 * @param url
	 *            the base url of the Sméagol server. For example:
	 *            http://www.example.com:1234/
	 * @throws MalformedURLException
	 *             if the provided url is not a valid URL
	 */
	public SmeagolClient(String url) throws MalformedURLException {
		URL serverUrl = new URL(url.endsWith("/") ? url : url + "/");
		client = Client.create();

		try {
			tag = client.resource(new URL(serverUrl, TAG_PATH).toURI());
			resource = client.resource(new URL(serverUrl, RESOURCE_PATH)
					.toURI());
			event = client.resource(new URL(serverUrl, EVENT_PATH).toURI());
			booking = client.resource(new URL(serverUrl, BOOKING_PATH).toURI());
		} catch (URISyntaxException e) {
			// this should never happen
			e.printStackTrace();
		}
	}

	/**
	 * Retrieve all tags defined in server.
	 * 
	 * @return a collection with all the tags defined on the server
	 * @throws JSONException
	 *             if the server response is not a invalid representation of a
	 *             tag list. This should only happen when there's not an Smeagol
	 *             v2 server listening on the URL provided to the constructor.
	 */
	public Collection<Tag> getTags() throws JSONException {
		String tagJsonArray = tag.accept(MediaType.APPLICATION_JSON_TYPE).get(
				String.class);
		return Tag.fromJSONArray(tagJsonArray);
	}

	/**
	 * Retrieve tag by id.
	 * 
	 * @param id
	 *            the id of the tag to retrieve.
	 * @return the tag
	 * @throws JSONException
	 */
	public Tag getTag(String id) throws JSONException {
		String json = tag.path(id).accept(MediaType.APPLICATION_JSON_TYPE).get(
				String.class);
		return Tag.fromJSONString(json);
	}
}