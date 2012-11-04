package edu.upc.cpl.smeagol.client;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Interval;
import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;

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
import edu.upc.cpl.smeagol.client.exception.SmeagolClientException;
import edu.upc.cpl.smeagol.json.DateTimeConverter;

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

	/*
	 * The following constants are the names of the parameters to be used in
	 * Form objects in PUTs and POSTs.
	 */

	public static final String TAG_ID_ATTR_NAME = "id";
	public static final String TAG_DESCRIPTION_ATTR_NAME = "description";

	public static final String RESOURCE_DESCRIPTION_ATTR_NAME = "description";
	public static final String RESOURCE_INFO_ATTR_NAME = "info";
	public static final String RESOURCE_TAGS_ATTR_NAME = "tags";

	public static final String EVENT_DESCRIPTION_ATTR_NAME = "description";
	public static final String EVENT_INFO_ATTR_NAME = "info";
	public static final String EVENT_STARTS_ATTR_NAME = "starts";
	public static final String EVENT_ENDS_ATTR_NAME = "ends";

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
	 *            the base url of the Sméagol server. For instance:
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
	public Tag getTag(String id) {
		ClientResponse response = tagWr.path(id).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

		if (response.getClientResponseStatus().equals(Status.NOT_FOUND)) {
			throw new NotFoundException("tag not found");
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
	public String createTag(String id, String description) {

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
		default:
			throw new SmeagolClientException("unexpected server status: "
					+ response.getClientResponseStatus());
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
	public void updateTag(String id, String newDescription) {

		Form f = new Form();
		f.add(TAG_ID_ATTR_NAME, id);
		f.add(TAG_DESCRIPTION_ATTR_NAME, newDescription);

		ClientResponse response = tagWr.path(id).accept(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, f);

		switch (response.getClientResponseStatus()) {
		case OK:
			// tag successfully updated
			break;
		case NOT_FOUND:
			throw new NotFoundException("tag not found");
		default:
			throw new SmeagolClientException("unexpected server status: "
					+ response.getClientResponseStatus());
		}
	}

	/**
	 * Deletes the <code>Tag</code> identified by <code>id</code>.
	 * 
	 * @param id
	 *            the identifier of the tag to be removed
	 * @throws NotFoundException
	 */
	public void deleteTag(String id) {
		ClientResponse response = tagWr.path(id).accept(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);

		if (response.getClientResponseStatus().equals(Status.NOT_FOUND)) {
			throw new NotFoundException("tag not found");
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
				// not found? OK. do nothing
			}
		}
		return result;
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
	public Resource getResource(Long id) {
		ClientResponse response = resourceWr.path(id.toString()).accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getClientResponseStatus().equals(Status.NOT_FOUND)) {
			throw new NotFoundException("resource not found");
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
	 *             if the provided description is not a valid Resource
	 *             description.
	 */
	public Long createResource(String description, String info) {
		Form f = new Form();
		f.add(RESOURCE_DESCRIPTION_ATTR_NAME, description);
		f.add(RESOURCE_INFO_ATTR_NAME, info);

		ClientResponse response = resourceWr.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, f);

		switch (response.getClientResponseStatus()) {
		case CONFLICT:
			throw new AlreadyExistsException("resource already exists");
		case BAD_REQUEST:
			throw new IllegalArgumentException();
		case CREATED:
			String id;
			try {
				id = getUriLastFragment(new URI(response.getHeaders().getFirst("Location"), false));
				return Long.parseLong(id);
			} catch (URIException e) {
				// this will never happen: the server returns well-formed URIs
			}
			break;
		default:
			throw new SmeagolClientException("unexpected server status: "
					+ response.getClientResponseStatus());
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
	public void deleteResource(Long id) {
		ClientResponse response = resourceWr.path(id.toString()).accept(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);

		if (response.getClientResponseStatus().equals(Status.NOT_FOUND)) {
			throw new NotFoundException("resource not found");
		}
	}

	/**
	 * Replace the Resource identified by {@code id} with a new Resource.
	 * 
	 * @param id
	 *            the identifier of the Resource to be updated.
	 * @param newResource
	 *            the Resource which will be used to update the old one.
	 * @throws AlreadyExistsException
	 */
	public void updateResource(long id, Resource newResource) throws NotFoundException,
			AlreadyExistsException {
		Form f = new Form();
		f.add(RESOURCE_DESCRIPTION_ATTR_NAME, newResource.getDescription());
		f.add(RESOURCE_INFO_ATTR_NAME, newResource.getInfo());

		ClientResponse response = resourceWr.path("" + id).accept(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, f);

		switch (response.getClientResponseStatus()) {
		case NOT_FOUND:
			throw new NotFoundException("resource not found");
		case CONFLICT:
			throw new AlreadyExistsException("resource already exists");
		case BAD_REQUEST:
			throw new IllegalArgumentException();
		case OK:
			break;
		default:
			throw new SmeagolClientException("unexpected server status: "
					+ response.getClientResponseStatus());
		}
	}

	/**
	 * Retrieve all {@code Event}s in server
	 * 
	 * @return a (possibly empty) collection containing all Events defined in
	 *         the Sméagol server.
	 */
	public Collection<Event> getEvents() {
		String eventJsonArray = eventWr.accept(MediaType.APPLICATION_JSON).get(String.class);

		return Event.deserializeCollection(eventJsonArray);
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
	public Event getEvent(long id) {
		ClientResponse response = eventWr.path(String.valueOf(id)).accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getClientResponseStatus().equals(Status.NOT_FOUND)) {
			throw new NotFoundException("event not found");
		}

		String json = response.getEntity(String.class);
		Event result = Event.deserialize(json);

		return result;
	}

	/**
	 * Creates a new {@code Event} in the server
	 * 
	 * @param description
	 *            the event description. Required, not blank.
	 * @param info
	 *            Event info. Optional.
	 * @param startEnd
	 *            the time interval (start, end) at which the event occurs.
	 *            Required.
	 * @return the identifier of the new event.
	 * 
	 * @throws URIException
	 */
	public Long createEvent(String description, String info, Interval startEnd) {
		if (startEnd == null) {
			throw new IllegalArgumentException("startEnd interval cannot be null");
		}
		Form f = new Form();
		f.add(EVENT_DESCRIPTION_ATTR_NAME, description);
		f.add(EVENT_INFO_ATTR_NAME, info);
		f.add(EVENT_STARTS_ATTR_NAME,
				ISODateTimeFormat.dateTimeNoMillis().print(new LocalDateTime(startEnd.getStart())));
		f.add(EVENT_ENDS_ATTR_NAME,
				ISODateTimeFormat.dateTimeNoMillis().print(new LocalDateTime(startEnd.getEnd())));

		ClientResponse response = eventWr.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, f);

		switch (response.getClientResponseStatus()) {
		case BAD_REQUEST:
			throw new IllegalArgumentException();
		case CREATED:
			try {
				URI locationHeader = new URI(response.getHeaders().getFirst("Location"), false);
				return Long.parseLong(getUriLastFragment(locationHeader));
			} catch (Exception e) {
				// This will never happen: server always returns well-formed
				// URIs
			}
		default:
			throw new SmeagolClientException("unexpected server status: "
					+ response.getClientResponseStatus());
		}
	}

	/**
	 * Replace Event identified by {@code id} with a new Event.
	 * 
	 * @param id
	 *            the identifier of the Event to update.
	 * @param newEvent
	 *            the Event which will be used to update the old Event.
	 * 
	 * @throws NotFoundException
	 */
	public void updateEvent(long id, Event newEvent) {
		Form f = new Form();
		f.add(EVENT_DESCRIPTION_ATTR_NAME, newEvent.getDescription());
		f.add(EVENT_INFO_ATTR_NAME, newEvent.getInfo());
		f.add(EVENT_STARTS_ATTR_NAME, DateTimeConverter.toSmeagolDateTime(newEvent.getInterval().getStart()));
		f.add(EVENT_ENDS_ATTR_NAME, DateTimeConverter.toSmeagolDateTime(newEvent.getInterval().getEnd()));

		ClientResponse response = eventWr.path("" + id).accept(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, f);

		switch (response.getClientResponseStatus()) {
		case NOT_FOUND:
			throw new NotFoundException("event not found");
		case BAD_REQUEST:
			throw new IllegalArgumentException();
		case OK:
			break;
		default:
			throw new SmeagolClientException("unexpected server status: "
					+ response.getClientResponseStatus());
		}
	}

	/**
	 * Delete an Event from the server.
	 * 
	 * @param id
	 *            the identifier of the event to delete.
	 * @throws NotFoundException
	 *             if there is no event with the provided id in the server.
	 */
	public void deleteEvent(long id) {
		ClientResponse response = eventWr.path("" + id).accept(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);

		if (response.getClientResponseStatus().equals(Status.NOT_FOUND)) {
			throw new NotFoundException("event not found");
		}
	}

	/**
	 * Apply a {@link Tag} to a {@link Resource}.
	 * 
	 * If the tag was already applied to the resource, this method does nothing.
	 * 
	 * @param tagId
	 *            the tag id. The tag must exist in the Sméagol server.
	 * 
	 * @param resourceId
	 *            the resource id. The resource must exist in the Sméagol
	 *            server.
	 * 
	 * @throws NotFoundException
	 *             if the tag or the resource do not exist in the Sméagol
	 *             server.
	 */
	public void tagResource(String tagId, long resourceId) {
		ClientResponse response = resourceWr.path(String.valueOf(resourceId)).path("tag").path(tagId)
				.accept(MediaType.APPLICATION_JSON).put(ClientResponse.class);

		switch (response.getClientResponseStatus()) {
		case OK:
			// done
			break;
		case NOT_FOUND:
			throw new NotFoundException("tag or resource not found");
		default:
			throw new SmeagolClientException("unexpected server status: "
					+ response.getClientResponseStatus());
		}
	}

	/**
	 * Get the {@link Tag}s currently applied to a {@link Resource}.
	 * 
	 * @param resourceId
	 *            the resource identifier. The resource must exist in the
	 *            Sméagol server.
	 * 
	 * @return the tags applied to the resource. Returns an empty collection if
	 *         no tags are applied to that resource.
	 * 
	 * @throws NotFoundException
	 *             if there is no resource with such id in the Sméagol server.
	 */
	public Collection<Tag> getResourceTags(long resourceId) {
		ClientResponse response = tagWr.queryParam("resource", String.valueOf(resourceId))
				.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

		switch (response.getClientResponseStatus()) {
		case NOT_FOUND:
			throw new NotFoundException("resource not found");
		case OK:
			String json = response.getEntity(String.class);
			return Tag.deserializeCollection(json);
		default:
			throw new SmeagolClientException("unexpected server status: "
					+ response.getClientResponseStatus());
		}

	}

	/**
	 * Makes a tag to be unapplied to a resource.
	 * 
	 * 
	 * @param tagId
	 *            the tag identifier. The tag must exist in the Sméagol server.
	 * 
	 * @param resourceId
	 *            the resource identifier. The resource must exist in the
	 *            Sméagol server.
	 * 
	 * @throws NotFoundException
	 *             if the tag or the resource do not exist in the Sméagol
	 *             server, or if the tag is not currently applied to that
	 *             resource.
	 */
	public void untagResource(String tagId, long resourceId) {
		ClientResponse response = resourceWr.path("" + resourceId).path("tag").path(tagId)
				.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);

		switch (response.getClientResponseStatus()) {
		case OK:
			// done
			break;
		case NOT_FOUND:
			throw new NotFoundException(
					"resource or tag not found; or tag is not currently applied to the resource");
		default:
			throw new SmeagolClientException("unexpected server status: "
					+ response.getClientResponseStatus());
		}
	}

	/**
	 * Apply a {@link Tag} to an {@link Event}.
	 * 
	 * If the tag was already applied to that event, this method does nothing.
	 * 
	 * @param tagId
	 *            the tag id. The tag must exist in the Sméagol server.
	 * @param eventId
	 *            the event id. The event must exist in the Sméagol server.
	 * 
	 * @throws NotFoundException
	 *             whether the tag or the event are not defined in the Sméagol
	 *             server.
	 */
	public void tagEvent(String tagId, long eventId) {
		ClientResponse response = eventWr.path("" + eventId).path("tag").path(tagId)
				.accept(MediaType.APPLICATION_JSON).put(ClientResponse.class);

		switch (response.getClientResponseStatus()) {
		case OK:
			// done
			break;
		case NOT_FOUND:
			throw new NotFoundException("tag or event not found");
		default:
			throw new SmeagolClientException("unexpected server status: "
					+ response.getClientResponseStatus());
		}
	}

	/**
	 * Get the tags applied to an event.
	 * 
	 * @return a (possibly empty) collection containing all the tags applied to
	 *         the event identified by {@code eventId}.
	 * 
	 * @throws NotFoundException
	 *             if there is no event in the Sméagol server with such
	 *             identifier.
	 */
	public Collection<Tag> getEventTags(long eventId) {
		ClientResponse response = tagWr.queryParam("event", String.valueOf(eventId))
				.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

		switch (response.getClientResponseStatus()) {
		case OK:
			String json = response.getEntity(String.class);
			return Tag.deserializeCollection(json);
		case NOT_FOUND:
			throw new NotFoundException("event not found");
		default:
			throw new SmeagolClientException("unexpected server status: "
					+ response.getClientResponseStatus());
		}
	}

}
