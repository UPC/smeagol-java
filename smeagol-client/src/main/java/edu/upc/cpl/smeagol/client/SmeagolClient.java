package edu.upc.cpl.smeagol.client;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

import edu.upc.cpl.smeagol.client.domain.Tag;
import edu.upc.cpl.smeagol.client.exception.AlreadyExistsException;
import edu.upc.cpl.smeagol.client.exception.NotFoundException;

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

	/* WebResource encapsulates a REST web resource */

	private WebResource tagWR;
	private WebResource resourceWR;
	private WebResource eventWR;
	private WebResource bookingWR;

	/**
	 * 
	 * @param url
	 *            the base url of the Sméagol server. For example:
	 *            http://www.example.com:3000/
	 * @throws MalformedURLException
	 *             if the provided url is not a valid URL
	 */
	public SmeagolClient(String url) throws MalformedURLException {
		URL serverUrl = new URL(url.endsWith("/") ? url : url + "/");
		client = Client.create();

		try {
			tagWR = client.resource(new URL(serverUrl, TAG_PATH).toURI());
			resourceWR = client.resource(new URL(serverUrl, RESOURCE_PATH).toURI());
			eventWR = client.resource(new URL(serverUrl, EVENT_PATH).toURI());
			bookingWR = client.resource(new URL(serverUrl, BOOKING_PATH).toURI());
		} catch (URISyntaxException e) {
			/*
			 * this should never happen, since we have been provided a valid url
			 * parameter (otherwise, a MalformedURLException would have been
			 * thrown during initialization of the serverUrl variable).
			 */
			e.printStackTrace();
		}
	}

	/**
	 * Retrieve all tags defined in server.
	 * 
	 * @return a collection containing all <code>Tag</code>s defined on the
	 *         server.
	 */
	public Collection<Tag> getTags() {
		String tagJsonArray = tagWR.accept(MediaType.APPLICATION_JSON).get(String.class);
		return Tag.deserializeCollection(tagJsonArray);
	}

	/**
	 * Retrieve <code>Tag</code> by id.
	 * 
	 * @param id
	 *            the id of the tag to retrieve.
	 * @return the tag
	 * @throws NotFoundException
	 *             when there is not a <code>Tag</code> in server with the
	 *             provided id.
	 */
	public Tag getTag(String id) throws NotFoundException {
		ClientResponse response = tagWR.path(id).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

		if (response.getClientResponseStatus().equals(Status.NOT_FOUND)) {
			throw new NotFoundException();
		}

		String json = response.getEntity(String.class);
		return Tag.deserialize(json);
	}

	/**
	 * Creates a new <code>Tag</code> in the server.
	 * 
	 * @param id
	 *            the id of the new tag, not null, not empty
	 * @param description
	 *            the description of the new tag
	 * @return the new <code>Tag</code>
	 * @throws IllegalArgumentException
	 *             if the id is null or an empty string, or if the lengths of
	 *             the provided arguments exceed {@link Tag.MAX_ID_LEN} and/or
	 *             {@link Tag.MAX_DESCRIPTION_LEN}.
	 * @throws AlreadyExistsException
	 *             if the server already contains a tag with the provided id
	 * @see Tag
	 * 
	 */
	public Tag createTag(String id, String description) throws IllegalArgumentException, AlreadyExistsException {
		Tag result = null;

		Form f = new Form();
		f.add(Tag.ID_ATTR_NAME, id);
		f.add(Tag.DESCRIPTION_ATTR_NAME, description);

		ClientResponse response = tagWR.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, f);

		switch (response.getClientResponseStatus()) {
		case CONFLICT:
			throw new AlreadyExistsException();
		case BAD_REQUEST:
			throw new IllegalArgumentException();
		case CREATED:
			result = new Tag(id, description);
			break;
		}

		return result;
	}

	/**
	 * Updates the description for the <code>Tag</code> identified by
	 * <code>id</code>.
	 * 
	 * @param id
	 * @param newDescription
	 *            the new tag description
	 * @return the updated <code>Tag</code>
	 * @throws NotFoundException
	 *             if the tag to be updated does not exist
	 */
	public Tag updateTag(String id, String newDescription) throws NotFoundException {
		Tag result = null;

		Form f = new Form();
		f.add(Tag.ID_ATTR_NAME, id);
		f.add(Tag.DESCRIPTION_ATTR_NAME, newDescription);

		ClientResponse response = tagWR.path(id).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, f);

		switch (response.getClientResponseStatus()) {
		case NOT_FOUND:
			throw new NotFoundException();
		case OK:
			result = new Tag(id, newDescription);
			break;
		}
		return result;
	}

	/**
	 * Deletes the <code>Tag</code> identified by <code>id</code>.
	 * 
	 * @param id
	 *            the identifier of the tag to be removed
	 * @throws NotFoundException
	 */
	public void deleteTag(String id) throws NotFoundException {
		ClientResponse response = tagWR.path(id).accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);

		if (response.getClientResponseStatus().equals(Status.NOT_FOUND)) {
			throw new NotFoundException();
		}
	}
}
