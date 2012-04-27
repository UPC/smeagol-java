package edu.upc.cpl.smeagol.client;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Interval;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

import edu.upc.cpl.smeagol.client.domain.Event;
import edu.upc.cpl.smeagol.client.domain.Resource;
import edu.upc.cpl.smeagol.client.domain.Tag;
import edu.upc.cpl.smeagol.client.exception.AlreadyExistsException;
import edu.upc.cpl.smeagol.client.exception.NotFoundException;

/**
 * This class implements a basic Sméagol client conforming to server API version
 * version 2.
 * 
 * @author angel
 * 
 */
public class SmeagolClient {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(SmeagolClient.class);

	private static final String TAG_PATH = "tag";
	private static final String RESOURCE_PATH = "resource";
	private static final String EVENT_PATH = "event";
	private static final String BOOKING_PATH = "booking";

	public static final String TAG_ID_ATTR_NAME = "id";
	public static final String TAG_DESCRIPTION_ATTR_NAME = "description";

	public static final String RESOURCE_DESCRIPTION_ATTR_NAME = "description";
	public static final String RESOURCE_INFO_ATTR_NAME = "info";
	public static final String RESOURCE_TAGS_ATTR_NAME = "tags";

	public static final String EVENT_DESCRIPTION_ATTR_NAME = "description";
	public static final String EVENT_INFO_ATTR_NAME = "info";
	public static final String EVENT_STARTS_ATTR_NAME = "starts";
	public static final String EVENT_ENDS_ATTR_NAME = "ends";
	public static final String EVENT_TAGS_ATTR_NAME = "tags";

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
	 * @return the id of the created tag
	 * @throws IllegalArgumentException
	 *             if the id is null or an empty string, or if the lengths of
	 *             the provided id or description exceed the maximum length
	 *             allowed by the server.
	 * @throws AlreadyExistsException
	 *             if the server already contains a tag with the provided id.
	 * @throws URIException
	 * @see Tag
	 * 
	 */
	public String createTag(String id, String description) throws AlreadyExistsException {

		Form f = new Form();
		f.add(TAG_ID_ATTR_NAME, id);
		f.add(TAG_DESCRIPTION_ATTR_NAME, description);

		ClientResponse response = tagWr.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, f);

		switch (response.getClientResponseStatus()) {
		case CONFLICT:
			throw new AlreadyExistsException();
		case BAD_REQUEST:
			throw new IllegalArgumentException();
		case CREATED:
			// tag was created successfully
			break;
		}

		try {
			URI uri = new URI(response.getHeaders().getFirst("Location"), false);
			return getUriLastFragment(uri);
		} catch (URIException e) {
			// this should never happen: server returns well-formed URIs
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Updates the description for the <code>Tag</code> identified by
	 * <code>id</code>.
	 * 
	 * @param id
	 * @param newDescription
	 *            the new tag description
	 * @throws NotFoundException
	 *             if the tag to be updated does not exist
	 */
	public void updateTag(String id, String newDescription) throws NotFoundException {

		Form f = new Form();
		f.add(TAG_ID_ATTR_NAME, id);
		f.add(TAG_DESCRIPTION_ATTR_NAME, newDescription);

		ClientResponse response = tagWr.path(id).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, f);

		switch (response.getClientResponseStatus()) {
		case NOT_FOUND:
			throw new NotFoundException();
		case OK:
			// tag successfully updated
			break;
		}
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
				Tag t = getTag(id);
				result.add(t);
			} catch (NotFoundException e) {
				// not found? ok. do nothing
			}
		}
		return result;
	}

	/**
	 * Create all tags in collection.
	 * <p>
	 * Existent tags will be ignored. Tags with invalid arguments will also be
	 * ignored.
	 * 
	 * @param tags
	 *            a collection of tags to be created
	 * @return the number of tags actually created (i.e. those which did not
	 *         exist in server yet).
	 */
	public int createTags(Collection<Tag> tags) {
		if (CollectionUtils.isEmpty(tags)) {
			return 0;
		}

		int count = 0;

		/*
		 * To save expensive network operations, we check locally wether they
		 * exist or not.
		 */
		HashSet<Tag> tagsInServer = new HashSet<Tag>(getTags());

		for (Tag t : tags) {
			if (!tagsInServer.contains(t)) {
				try {
					createTag(t.getId(), t.getDescription());
					count++;
				} catch (IllegalArgumentException e) {
					// tag ignored, counter not incremented
				} catch (AlreadyExistsException e) {
					// this should never happen, as this is checked the "if"
					// condition
					e.printStackTrace();
				}
			}
		}
		return count;
	}

	/**
	 * Retrieve all <code>Resource</code>s defined in server.
	 * 
	 * @return a collection containing all the resources defined in the the
	 *         server.
	 */
	public Collection<Resource> getResources() {
		String resourceJsonArray = resourceWr.accept(MediaType.APPLICATION_JSON).get(String.class);

		return Resource.deserializeCollection(resourceJsonArray);
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
		return Resource.deserialize(json);
	}

	/**
	 * Create a new resource in the server.
	 * 
	 * @param description
	 *            the description of the new resource, must be a not null,
	 *            non-blank string.
	 * @param info
	 *            additional information of the new resource
	 * @return the identifier for the resource just created
	 * @throws AlreadyExistsException
	 *             if there is already another resource with the same
	 *             description defined in the server.
	 * @throws IllegalArgumentException
	 *             if the provided description is <code>null</code>, an empty
	 *             string, or a blank string.
	 */
	public Long createResource(String description, String info) throws AlreadyExistsException {
		Form f = new Form();
		f.add(RESOURCE_DESCRIPTION_ATTR_NAME, description);
		f.add(RESOURCE_INFO_ATTR_NAME, info);

		ClientResponse response = resourceWr.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, f);

		switch (response.getClientResponseStatus()) {
		case CONFLICT:
			throw new AlreadyExistsException();
		case BAD_REQUEST:
			throw new IllegalArgumentException();
		case CREATED:
			String id;
			try {
				id = getUriLastFragment(new URI(response.getHeaders().getFirst("Location"), false));
				return Long.parseLong(id);
			} catch (URIException e) {
				// this will never happen: the server returns well-formed URIs
				logger.error("error parsing Resource location provided by server", e);
			}
			break;
		}
		return null;
	}

	private String getUriLastFragment(URI locationHeader) {
		String[] fragments = StringUtils.split(locationHeader.toString(), "/");
		return fragments[fragments.length - 1];
	}

	/**
	 * Delete existing resource from server.
	 * 
	 * @param id
	 *            the identifier for the resource to delete.
	 * @throws NotFoundException
	 *             if there is no such resource with the provided identifier.
	 */
	public void deleteResource(Long id) throws NotFoundException {
		ClientResponse response = resourceWr.path(id.toString()).accept(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);

		if (response.getClientResponseStatus().equals(Status.NOT_FOUND)) {
			throw new NotFoundException();
		}
	}

	/**
	 * Retrieve all {@code Event}s in server
	 * 
	 * @return a (possibly empty) collection containing all the Events defined
	 *         in the Smeagol server
	 */
	public Collection<Event> getEvents() {
		String eventJsonArray = eventWr.accept(MediaType.APPLICATION_JSON).get(String.class);

		Collection<Event> events = Event.deserializeCollection(eventJsonArray);

		// In a Event list, the server only returns the tag identifiers
		// instead of the full tag tag list for every event. For each
		// identifier, we must retrieve the full tag attributes and repopulate
		// the Resource tags.

		for (Event e : events) {
			populateTagAttributes(e.getTags());
		}
		return events;
	}

	/**
	 * Retrieve an {@code Event} by its id
	 * 
	 * @param id
	 *            the identifier of the {@code Event} to retrieve
	 * @return the requested {@code Event}, if exists
	 * @throws NotFoundException
	 *             if there is no {@code Event} in the server with such id
	 */
	public Event getEvent(Long id) throws NotFoundException {
		if (id == null) {
			throw new IllegalArgumentException("getEvent requires a non null argument");
		}

		ClientResponse response = eventWr.path(id.toString()).accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getClientResponseStatus().equals(Status.NOT_FOUND)) {
			throw new NotFoundException();
		}

		String json = response.getEntity(String.class);
		Event result = Event.deserialize(json);

		// Populate tag descriptions before returning the event to the user
		populateTagAttributes(result.getTags());

		return result;
	}

	/**
	 * Creates a new {@code Event} in the server
	 * 
	 * @param description
	 *            the event description. Cannot be null.
	 * @param info
	 *            optional info for the event.
	 * @param startEnd
	 *            the time interval (start, end) at which the event occurs.
	 * @return the identifier of the new event.
	 * @throws NullPointerException
	 * @throws URIException
	 */
	public Long createEvent(String description, String info, Interval startEnd) {
		Form f = new Form();
		f.add(EVENT_DESCRIPTION_ATTR_NAME, description);
		f.add(EVENT_INFO_ATTR_NAME, info);
		f.add(EVENT_STARTS_ATTR_NAME, startEnd.getStart().toString());
		f.add(EVENT_ENDS_ATTR_NAME, startEnd.getEnd().toString());

		ClientResponse response = eventWr.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, f);

		switch (response.getClientResponseStatus()) {
		case BAD_REQUEST:
			throw new IllegalArgumentException();
		case CREATED:
			URI locationHeader;
			try {
				locationHeader = new URI(response.getHeaders().getFirst("Location"), false);
				return Long.parseLong(getUriLastFragment(locationHeader));
			} catch (Exception e) {
				// This will never happen: server always returns well-formed
				// URIs
				logger.error("error parsing Event location provided by server", e);
			}
		}
		return null;
	}

	/**
	 * Converts a {@code Tag} collection into a String containing their
	 * identifiers separated by commas. This is a utility method to prepare
	 * {@code Form} parameters.
	 * 
	 * @param tags
	 * @return
	 */
	private String tagsAsFormParameter(Collection<Tag> tags) {
		if (CollectionUtils.isEmpty(tags)) {
			return "";
		}
		Set<String> tagIds = new HashSet<String>();
		for (Tag t : tags) {
			tagIds.add(t.getId());
		}
		return StringUtils.join(tagIds, ",");
	}

	/**
	 * Retrieve and populate {@code Tag} descriptions for a collection of Tags.
	 * Tag descriptions will be retrieved from the server. Tags which don't
	 * exist in server will be ignored (their descriptions will not be
	 * modified).
	 * 
	 * @param tagsToPopulate
	 *            a collection of tags with possibly empty descriptions.
	 */
	private void populateTagAttributes(Collection<Tag> tagsToPopulate) {
		for (Tag t : tagsToPopulate) {
			try {
				Tag aux = getTag(t.getId());
				t.setDescription(aux.getDescription());
			} catch (NotFoundException e) {
				// This tag does not exist in server. Just ignore it.
			}
		}
	}

	/**
	 * Replace Event identified by {@code id} with a new Event.
	 * 
	 * @param id
	 *            the identifier of the Event to update.
	 * @param newEvent
	 *            the Event which will be used to update the old Event.
	 */
	public Event updateEvent(long id, Event newEvent) throws NotFoundException {
		Event result = null;

		Form f = new Form();
		f.add(EVENT_DESCRIPTION_ATTR_NAME, newEvent.getDescription());
		f.add(EVENT_INFO_ATTR_NAME, newEvent.getInfo());
		f.add(EVENT_STARTS_ATTR_NAME, newEvent.getInterval().getStart());
		f.add(EVENT_ENDS_ATTR_NAME, newEvent.getInterval().getEnd());
		f.add(EVENT_TAGS_ATTR_NAME, tagsAsFormParameter(newEvent.getTags()));

		ClientResponse response = eventWr.path("" + id).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, f);

		switch (response.getClientResponseStatus()) {
		case NOT_FOUND:
			throw new NotFoundException();
		case OK:
			result = Event.deserialize(response.getEntity(String.class));
			populateTagAttributes(result.getTags());
			break;
		}
		return result;
	}

	/**
	 * Delete an Event from the server.
	 * 
	 * @param id
	 *            the identifier of the event to delete.
	 * @throws NotFoundException
	 *             if there is no event with the provided id in the server.
	 */
	public void deleteEvent(long id) throws NotFoundException {
		ClientResponse response = eventWr.path("" + id).accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);

		if (response.getClientResponseStatus().equals(Status.NOT_FOUND)) {
			throw new NotFoundException();
		}
	}
}
