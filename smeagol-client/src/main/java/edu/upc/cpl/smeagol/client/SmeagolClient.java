package edu.upc.cpl.smeagol.client;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

import edu.upc.cpl.smeagol.client.domain.Resource;
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

	private WebResource tagWr;
	private WebResource resourceWr;
	private WebResource eventWr;
	private WebResource bookingWr;

	/**
	 * Sméagol client constructor.
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
			tagWr = client.resource(new URL(serverUrl, TAG_PATH).toURI());
			resourceWr = client.resource(new URL(serverUrl, RESOURCE_PATH).toURI());
			eventWr = client.resource(new URL(serverUrl, EVENT_PATH).toURI());
			bookingWr = client.resource(new URL(serverUrl, BOOKING_PATH).toURI());
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
		String tagJsonArray = tagWr.accept(MediaType.APPLICATION_JSON).get(String.class);
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
		ClientResponse response = tagWr.path(id).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

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
	 *             the provided id or description exceed the maximum length
	 *             allowed by the server.
	 * @throws AlreadyExistsException
	 *             if the server already contains a tag with the provided id.
	 * @see Tag
	 * 
	 */
	public Tag createTag(String id, String description) throws IllegalArgumentException, AlreadyExistsException {
		Tag result = null;

		Form f = new Form();
		f.add(Tag.ID_ATTR_NAME, id);
		f.add(Tag.DESCRIPTION_ATTR_NAME, description);

		ClientResponse response = tagWr.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, f);

		switch (response.getClientResponseStatus()) {
		case CONFLICT:
			throw new AlreadyExistsException();
		case BAD_REQUEST:
			throw new IllegalArgumentException();
		case CREATED:
			result = Tag.deserialize(response.getEntity(String.class));
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

		ClientResponse response = tagWr.path(id).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, f);

		switch (response.getClientResponseStatus()) {
		case NOT_FOUND:
			throw new NotFoundException();
		case OK:
			result = Tag.deserialize(response.getEntity(String.class));
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
		ClientResponse response = tagWr.path(id).accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);

		if (response.getClientResponseStatus().equals(Status.NOT_FOUND)) {
			throw new NotFoundException();
		}
	}

	/**
	 * Returns all tags defined in server whose identifiers are in the provided
	 * list.
	 * 
	 * @param identifiers
	 *            a list of valid tag identifiers
	 * @return a collection containing all the tags defined in the server.
	 */
	public Collection<Tag> getTags(Collection<String> identifiers) {
		Collection<Tag> result = new HashSet<Tag>();
		for (String id : identifiers) {
			try {
				result.add(getTag(id));
			} catch (NotFoundException e) {
				// not found? ok. do nothing
			}
		}
		return result;
	}

	/**
	 * Retrieve all <code>Resource</code>s defined in server.
	 * 
	 * @return a collection containing all the resources defined in the server.
	 */
	public Collection<Resource> getResources() {
		String resourceJsonArray = resourceWr.accept(MediaType.APPLICATION_JSON).get(String.class);

		Collection<Resource> resources = Resource.deserializeCollection(resourceJsonArray);

		/*
		 * In a Resource list, the server only returns the tag identifiers
		 * instead of the full tag tag list for every resource. For each
		 * identifier, we must retrieve the full tag attributes and repopulate
		 * the Resource tags.
		 */
		for (Resource r : resources) {
			Collection<String> ids = new HashSet<String>();
			for (Tag t : r.getTags()) {
				ids.add(t.getId());
			}
			r.setTags(getTags(ids));
		}
		return resources;
	}

	/**
	 * Retrieve the resource whith the provided identifier.
	 * 
	 * @param id
	 *            the resource identifier
	 * @return the resource with the provided identifier, if exists.
	 * @throws NotFoundException
	 *             if there is no resource with such identifier.
	 */
	public Resource getResource(Long id) throws NotFoundException {
		ClientResponse response = resourceWr.path(id.toString()).accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getClientResponseStatus().equals(Status.NOT_FOUND)) {
			throw new NotFoundException();
		}

		String json = response.getEntity(String.class);
		Resource result = Resource.deserialize(json);

		/* Populate tag descriptions before returning the resource to the user */
		HashSet<String> ids = new HashSet<String>();
		for (Tag t : result.getTags()) {
			ids.add(t.getId());
		}
		result.setTags(getTags(ids));

		return result;
	}

	/**
	 * Create a new resource in the server.
	 * 
	 * @param description
	 *            the description of the new resource, must be a not null,
	 *            non-blank string.
	 * @param info
	 *            additional information of the new resource
	 * @param tags
	 *            an optional collection of tags to assign to the resource. If
	 *            the collection contains any non-existent tags, they will be
	 *            created in the server.
	 * @return the resource just created
	 * @throws AlreadyExistsException
	 *             if there is already another resource with the same
	 *             description defined in the server.
	 * @throws IllegalArgumentException
	 *             if the provided description is <code>null</code>, an empty
	 *             string, or a blank string.
	 */
	public Resource createResource(String description, String info, Collection<Tag> tags)
			throws AlreadyExistsException, IllegalArgumentException {
		Form f = new Form();
		f.add(Resource.DESCRIPTION_ATTR_NAME, description);
		f.add(Resource.INFO_ATTR_NAME, info);

		ClientResponse response = resourceWr.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, f);

		Resource result = null;

		switch (response.getClientResponseStatus()) {
		case CONFLICT:
			throw new AlreadyExistsException();
		case BAD_REQUEST:
			throw new IllegalArgumentException();
		case CREATED:
			result = Resource.deserialize(response.getEntity(String.class));
			try {
				result = getResource(result.getId());
			} catch (NotFoundException e) {
				/*
				 * This should not happen, as we are retrieving the resource we
				 * just created.
				 */
				e.printStackTrace();
			}
			break;
		}
		return result;
	}
}
