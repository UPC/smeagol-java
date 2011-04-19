package edu.upc.cpl.smeagol.client;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import edu.upc.cpl.smeagol.client.domain.Event;
import edu.upc.cpl.smeagol.client.domain.Resource;
import edu.upc.cpl.smeagol.client.domain.Tag;
import edu.upc.cpl.smeagol.client.exception.AlreadyExistsException;
import edu.upc.cpl.smeagol.client.exception.NotFoundException;

/**
 * <code>SmeagolClient</code> tests.
 * <p>
 * WARNING: Before running these tests
 * 
 * @author angel
 * 
 */
@RunWith(JUnit4.class)
public class SmeagolClientTest extends TestCase {

	private static Logger logger = Logger.getLogger(SmeagolClientTest.class);

	/**
	 * url for test server
	 */
	private static final String SERVER_URL = "http://localhost:3000";

	private static int TAG_COUNT = 8; // number of tags in server
	private static int RESOURCE_COUNT = 5; // number of resources in server
	private static int EVENT_COUNT = 4; // number of events in server

	private static final Tag EXISTENT_TAG = new Tag("videoconferencia", "descr 3");
	private static final Tag NEW_TAG = new Tag("newtag",
			"description for a new tag with a really really really really really "
					+ "really really really really really really long description");
	private static final Tag NON_EXISTENT_TAG = new Tag("nonexistent", "non existent description");
	private static final Tag PROJECTOR = new Tag("projector", "descr 1");

	private static final Long EXISTENT_RESOURCE_ID = 2L;
	private static final Collection<Tag> EXISTENT_RESOURCE_TAGS = new TreeSet<Tag>();
	private static final Resource EXISTENT_RESOURCE = new Resource("Aula test 2", "Estem de proves");
	private static final Resource NEW_RESOURCE = new Resource("New resource desc", "New resource info");
	private static final Collection<Tag> NEW_RESOURCE_TAGS = new TreeSet<Tag>();
	private static final Long NON_EXISTENT_RESOURCE_ID = 2000L;
	private static final Resource NON_EXISTENT_RESOURCE = new Resource("NON EXISTENT RESOURCE",
			"NON EXISTENT DESCRIPTION");

	private static final long EXISTENT_EVENT_ID = 4L;
	private static final Event EXISTENT_EVENT = new Event("Descripció de l'event 4", "Informació 4", new Interval(
			new DateTime("2011-02-16T04:00:00"), new DateTime("2011-02-16T05:00:00")));
	private static final long NON_EXISTENT_EVENT_ID = 4000L;
	private static final Event NON_EXISTENT_EVENT = new Event("NON EXISTENT EVENT", "NON EXISTENT EVENT INFO",
			new Interval(new DateTime("2000-01-01T08:00:00"), new DateTime("2000-01-01T10:00:00")),
			new ArrayList<Tag>());

	static {
		logger.info("*******************************************************************************");
		logger.info("NOTE: Before running these tests, check that a compatible Smeagol server       ");
		logger.info("is running on " + SERVER_URL);
		logger.info("Anyway, you should use the file src/test/resources/db-test.sql to recreate the ");
		logger.info("server database before running the test suite.                                 ");
		logger.info("*******************************************************************************");
	}

	private static SmeagolClient client;

	@Before
	public void setUp() throws Exception {
		client = new SmeagolClient(SERVER_URL);

		EXISTENT_RESOURCE.setId(EXISTENT_RESOURCE_ID);
		EXISTENT_RESOURCE_TAGS.add(new Tag("pantalla", "descr 2"));
		EXISTENT_RESOURCE_TAGS.add(new Tag("wireless", "descr 8"));
		EXISTENT_RESOURCE.setTags(EXISTENT_RESOURCE_TAGS);

		NEW_RESOURCE_TAGS.add(new Tag("newtag1", "newtag1 description"));
		NEW_RESOURCE_TAGS.add(new Tag("newtag2", "newtag2 description"));
		NEW_RESOURCE_TAGS.add(new Tag("newtag3", "newtag3 description"));
		NEW_RESOURCE.setTags(NEW_RESOURCE_TAGS);

		NON_EXISTENT_RESOURCE.setId(NON_EXISTENT_RESOURCE_ID);

		EXISTENT_EVENT.setId(EXISTENT_EVENT_ID);
	}

	@Test(expected = MalformedURLException.class)
	public void testConstructorWithMalformedUrl() throws MalformedURLException {
		@SuppressWarnings("unused")
		SmeagolClient sc = new SmeagolClient("this is not a valid url");
	}

	@Test
	public void testGetTags() {
		Collection<Tag> tags = client.getTags();
		assertTrue(tags.size() == TAG_COUNT);
		assertTrue(tags.contains(EXISTENT_TAG));
	}

	@Test
	public void testGetTag() throws NotFoundException {
		Tag t = client.getTag(EXISTENT_TAG.getId());
		assertTrue(t != null);
		assertEquals(EXISTENT_TAG.getId(), t.getId());
		assertEquals(EXISTENT_TAG.getDescription(), t.getDescription());
	}

	@Test(expected = NotFoundException.class)
	public void testGetTagNotFound() throws NotFoundException {
		@SuppressWarnings("unused")
		Tag t = client.getTag(NON_EXISTENT_TAG.getId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateTagIllegalArgument() throws IllegalArgumentException, AlreadyExistsException {
		@SuppressWarnings("unused")
		Tag t = client.createTag(null, null);
	}

	@Test(expected = AlreadyExistsException.class)
	public void testCreateTagAlreadyExists() throws IllegalArgumentException, AlreadyExistsException {
		@SuppressWarnings("unused")
		Tag t = client.createTag(EXISTENT_TAG.getId(), null);
		fail("test create existent tag");
	}

	@Test
	public void testCreateTagWithNullDescription() throws IllegalArgumentException, AlreadyExistsException {
		Tag aux = new Tag("aux", null);
		Tag t = client.createTag(aux.getId(), aux.getDescription());
		assertEquals(aux, t);
		assertEquals(TAG_COUNT + 1, client.getTags().size());
		TAG_COUNT++;
	}

	@Test
	public void testCreateTag() throws IllegalArgumentException, AlreadyExistsException {
		Tag t = client.createTag(NEW_TAG.getId(), NEW_TAG.getDescription());
		assertEquals(NEW_TAG, t);
		assertEquals(TAG_COUNT + 1, client.getTags().size());
		TAG_COUNT++;
	}

	@Test(expected = NotFoundException.class)
	public void testDeleteTagNotFound() throws NotFoundException {
		client.deleteTag("TrulyInexistentTag");
		fail("delete non existent tag");
	}

	@Test
	public void testDeleteTag() throws NotFoundException {
		Collection<Tag> before = client.getTags();
		assertTrue(before.contains(PROJECTOR));
		client.deleteTag(PROJECTOR.getId());
		Collection<Tag> after = client.getTags();
		assertEquals(before.size() - 1, after.size());
		TAG_COUNT--;
		assertFalse(after.contains(PROJECTOR));
	}

	@Test
	public void testUpdateTag() throws NotFoundException {
		String OLD_DESCRIPTION = EXISTENT_TAG.getDescription();
		String NEW_DESCRIPTION = "hi! am a new description";
		Collection<Tag> tags = client.getTags();

		assertTrue(tags.contains(EXISTENT_TAG));
		client.updateTag(EXISTENT_TAG.getId(), NEW_DESCRIPTION);
		Tag updated = client.getTag(EXISTENT_TAG.getId());
		assertEquals(EXISTENT_TAG.getId(), updated.getId());
		assertEquals(NEW_DESCRIPTION, updated.getDescription());
		client.updateTag(EXISTENT_TAG.getId(), OLD_DESCRIPTION);
		Tag old = client.getTag(EXISTENT_TAG.getId());
		assertEquals(EXISTENT_TAG.getDescription(), old.getDescription());
	}

	@Test(expected = NotFoundException.class)
	public void testUpdateTagNotFound() throws NotFoundException {
		Collection<Tag> tags = client.getTags();
		assertFalse(tags.contains(NON_EXISTENT_TAG));
		client.updateTag("dummy100", "dummy value");
	}

	@Test
	public void testGetResources() {
		Collection<Resource> resources = client.getResources();
		assertEquals(RESOURCE_COUNT, resources.size());
		assertTrue(resources.contains(EXISTENT_RESOURCE));
	}

	@Test(expected = NotFoundException.class)
	public void testGetResourceNotFound() throws NotFoundException {
		@SuppressWarnings("unused")
		Resource r = client.getResource(NON_EXISTENT_RESOURCE.getId());
	}

	@Test
	public void testGetResource() throws NotFoundException {
		Resource r = client.getResource(EXISTENT_RESOURCE.getId());
		assertNotNull(r);
		assertEquals(EXISTENT_RESOURCE, r);
	}

	@Test(expected = AlreadyExistsException.class)
	public void testCreateDuplicatedResource() throws AlreadyExistsException, IllegalArgumentException {
		Collection<Resource> resources = client.getResources();
		assertTrue(resources.contains(EXISTENT_RESOURCE));
		@SuppressWarnings("unused")
		Resource r = client.createResource(EXISTENT_RESOURCE.getDescription(), null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateResourceWithNullDescription() throws AlreadyExistsException, IllegalArgumentException {
		String NULL_STR = null;

		@SuppressWarnings("unused")
		Resource r = client.createResource(NULL_STR, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateResourceWithEmptyDescription() throws AlreadyExistsException, IllegalArgumentException {
		String EMPTY_STR = "";

		@SuppressWarnings("unused")
		Resource r = client.createResource(EMPTY_STR, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateResourceWithBlankDescription() throws AlreadyExistsException, IllegalArgumentException {
		String BLANKS_STR = "         ";

		@SuppressWarnings("unused")
		Resource r = client.createResource(BLANKS_STR, null, null);
	}

	@Test
	public void testCreateResource() throws AlreadyExistsException, IllegalArgumentException {
		Collection<Resource> resources = client.getResources();
		assertEquals(RESOURCE_COUNT, resources.size());
		assertFalse(resources.contains(NEW_RESOURCE));
		Resource r = client.createResource(NEW_RESOURCE.getDescription(), NEW_RESOURCE.getInfo(),
				NEW_RESOURCE.getTags());
		resources = client.getResources();
		NEW_RESOURCE.setId(r.getId());
		assertEquals(NEW_RESOURCE, r);
		assertEquals(++RESOURCE_COUNT, resources.size());
		assertTrue(resources.contains(NEW_RESOURCE));
	}

	@Test(expected = NotFoundException.class)
	public void testDeleteResourceNotFound() throws NotFoundException {
		Collection<Resource> resources = client.getResources();
		assertEquals(RESOURCE_COUNT, resources.size());
		client.deleteResource(NON_EXISTENT_RESOURCE_ID);
	}

	@Test
	public void testDeleteResource() {
		Collection<Resource> resources = client.getResources();
		assertEquals(RESOURCE_COUNT, resources.size());
		try {
			client.deleteResource(EXISTENT_RESOURCE_ID);
		} catch (NotFoundException e) {
			fail("delete resource");
		}
		Collection<Resource> resourcesAfter = client.getResources();
		assertFalse(resourcesAfter.contains(EXISTENT_RESOURCE));
		assertEquals(--RESOURCE_COUNT, resourcesAfter.size());
	}

	@Test
	public void testGetEvents() {
		ArrayList<Event> events = new ArrayList<Event>(client.getEvents());

		assertEquals(EVENT_COUNT, events.size());
		for (Event e : events) {
			logger.debug(e);
		}
		assertEquals(EXISTENT_EVENT, events.get(3));
		assertTrue(events.contains(EXISTENT_EVENT));
	}

	@Test(expected = NotFoundException.class)
	public void testGetEventNotFound() throws NotFoundException {
		@SuppressWarnings("unused")
		Event event = client.getEvent(NON_EXISTENT_EVENT_ID);
	}

	@Test
	public void testGetEvent() {
		fail("not yet implemented");
	}

	@Test
	public void testCreateEvent() {
		fail("not yet implemented");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateEventWithInvalidDescription() {
		// test exceptions with blank empty and null description
	}

}
