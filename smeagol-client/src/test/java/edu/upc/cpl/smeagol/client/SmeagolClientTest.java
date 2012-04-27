package edu.upc.cpl.smeagol.client;

import java.net.MalformedURLException;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import edu.upc.cpl.smeagol.client.db.DbUtils;
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

	@Rule
	public static final DbUtils dbUtils = new DbUtils();

	private static final Tag TAG_WITH_NULL_DESCRIPTION = new Tag("tag", null);
	private static final Tag TAG_1 = new Tag("tag1", "tag 1 description");
	private static final Tag TAG_2 = new Tag("tag2", "tag 2 description");

	private static final Resource RESOURCE_1 = new Resource("resource 1", "resource 1 info");

	private static final Event EVENT_1 = new Event("event 1", "event 1 info", new Interval(new DateTime(
			"2011-04-20T08:00:00"), new DateTime("2011-04-25T14:00:00")));

	private static SmeagolClient client;

	@BeforeClass
	public static void prepareTestEnvironment() {

		if (StringUtils.isBlank(dbUtils.getServerUrl())) {
			logger.error("Please set the " + DbUtils.ENV_SMEAGOL_URL_NAME
					+ " environment variable with the URL of the Sméagol server you want to run the tests with "
					+ "(e.g. 'export " + DbUtils.ENV_SMEAGOL_URL_NAME + "=http://localhost:3000').");
			System.exit(1);
		}
		if (StringUtils.isBlank(dbUtils.getDatabasePath())) {
			logger.error("You must set the " + DbUtils.ENV_SMEAGOL_DB_PATH_NAME
					+ " environment variable with the path to the Sméagol server database used to run the tests "
					+ "(e.g. 'export " + DbUtils.ENV_SMEAGOL_DB_PATH_NAME + "=/path/to/smeagol/var/smeagol.db').");
			System.exit(1);
		}

		System.out.println("Running " + SmeagolClient.class.getCanonicalName()
				+ " tests with the following configuration: ");
		System.out.println("");
		System.out.println("  * " + DbUtils.ENV_SMEAGOL_URL_NAME + " = " + dbUtils.getServerUrl());
		System.out.println("  * " + DbUtils.ENV_SMEAGOL_DB_PATH_NAME + " = " + dbUtils.getDatabasePath());
		System.out.println("");

		try {
			client = new SmeagolClient(dbUtils.getServerUrl());
		} catch (MalformedURLException ex) {
			logger.error("error creating SmeagolClient instance", ex);
		}
	}

	@Test(expected = MalformedURLException.class)
	public void testConstructorWithMalformedUrl() throws MalformedURLException {
		@SuppressWarnings("unused")
		SmeagolClient sc = new SmeagolClient("this is not a valid url");
	}

	@Test
	public void testGetTagsWhenNoTagsOnServer() {
		Collection<Tag> tags = client.getTags();
		assertTrue(tags.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateTagWithNullId() throws AlreadyExistsException {
		client.createTag(null, "a description");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateTagWithIdTooLong() throws AlreadyExistsException {
		client.createTag(StringUtils.rightPad("a", Tag.ID_MAX_LEN + 1, "a"), "a description");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateTagWithDescriptionTooLong() throws AlreadyExistsException {
		client.createTag("a", StringUtils.rightPad("x", Tag.DESCRIPTION_MAX_LEN + 1, "x"));
	}

	@Test
	public void testCreateTagWithNullDescription() throws AlreadyExistsException, NotFoundException {
		int tagsInServer = client.getTags().size();
		Tag aux = TAG_WITH_NULL_DESCRIPTION;
		String tagId = client.createTag(aux.getId(), aux.getDescription());
		assertEquals(TAG_WITH_NULL_DESCRIPTION.getId(), tagId);
		Tag g = client.getTag(tagId);
		assertEquals(aux, g);
		assertEquals(tagsInServer + 1, client.getTags().size());
	}

	@Test
	public void testCreateTag() throws AlreadyExistsException, NotFoundException {
		int tagsInServer = client.getTags().size();
		client.createTag(TAG_1.getId(), TAG_1.getDescription());
		assertEquals(tagsInServer + 1, client.getTags().size());
		assertEquals(TAG_1, client.getTag(TAG_1.getId()));
		client.createTag(TAG_2.getId(), TAG_2.getDescription());
		assertEquals(TAG_2, client.getTag(TAG_2.getId()));
		assertEquals(tagsInServer + 2, client.getTags().size());
	}

	@Test(expected = AlreadyExistsException.class)
	public void testCreateTagAlreadyExists() throws AlreadyExistsException {
		String id = client.createTag(TAG_1.getId(), null);
		assertNotNull(id);
		client.createTag(id, "some other description");
	}

	@Test(expected = NotFoundException.class)
	public void testGetTagNotFound() throws NotFoundException {
		@SuppressWarnings("unused")
		Tag t = client.getTag("dummy");
	}

	public void testGetTags() {
		Collection<Tag> tags = client.getTags();
		tags.contains(TAG_1);
	}

	@Test
	public void testUpdateTag() throws NotFoundException, AlreadyExistsException {
		String NEW_DESCRIPTION = "hi! am a new description á é";
		String id = client.createTag(TAG_1.getId(), TAG_1.getDescription());
		client.updateTag(id, NEW_DESCRIPTION);
		Tag updated = client.getTag(id);
		assertEquals(TAG_1.getId(), updated.getId());
		assertEquals(NEW_DESCRIPTION, updated.getDescription());
	}

	@Test(expected = NotFoundException.class)
	public void testUpdateTagNotFound() throws NotFoundException {
		Tag NON_EXISTENT_TAG = new Tag("dummy", "dummy description");
		Collection<Tag> tags = client.getTags();
		assertFalse(tags.contains(NON_EXISTENT_TAG));
		client.updateTag("dummy100", "dummy value");
	}

	@Test(expected = NotFoundException.class)
	public void testDeleteTagNotFound() throws NotFoundException {
		client.deleteTag("TrulyInexistentTag");
	}

	@Test
	public void testDeleteTag() throws NotFoundException, AlreadyExistsException {
		client.createTag(TAG_1.getId(), TAG_1.getDescription());
		client.createTag(TAG_2.getId(), TAG_2.getDescription());
		Collection<Tag> tags = client.getTags();
		assertTrue(tags.contains(TAG_1));
		assertTrue(tags.contains(TAG_2));
		assertEquals(2, tags.size());
		client.deleteTag(TAG_1.getId());
		Collection<Tag> tagsAfter = client.getTags();
		assertFalse(tagsAfter.contains(TAG_1));
		assertTrue(tagsAfter.contains(TAG_2));
		client.deleteTag(TAG_2.getId());
		tagsAfter = client.getTags();
		assertFalse(client.getTags().contains(TAG_2));
	}

	@Test
	public void testGetResourcesWithNoResources() {
		Collection<Resource> resources = client.getResources();
		assertTrue(CollectionUtils.isEmpty(resources));
	}

	@Test(expected = NotFoundException.class)
	public void testGetResourceNotFound() throws NotFoundException {

		@SuppressWarnings("unused")
		Resource r = client.getResource(123456789L);
	}

	@Test
	public void testCreateResource() throws AlreadyExistsException, NotFoundException {
		Collection<Resource> resourcesBefore = client.getResources();
		int resourceCountBefore = resourcesBefore.size();

		assertFalse(resourcesBefore.contains(RESOURCE_1));
		long resourceId = client.createResource(RESOURCE_1.getDescription(), RESOURCE_1.getInfo());
		Collection<Resource> resourcesAfter = client.getResources();
		RESOURCE_1.setId(resourceId);
		assertEquals(RESOURCE_1, client.getResource(resourceId));
		assertEquals(resourceCountBefore + 1, resourcesAfter.size());
		assertTrue(resourcesAfter.contains(RESOURCE_1));
	}

	@Test
	public void testGetResource() throws NotFoundException, AlreadyExistsException {
		assertFalse(client.getResources().contains(RESOURCE_1));
		Long id = client.createResource(RESOURCE_1.getDescription(), RESOURCE_1.getInfo());
		Resource r = client.getResource(id);
		assertNotNull(r);
		RESOURCE_1.setId(id);
		assertEquals(RESOURCE_1, r);
	}

	@Test(expected = AlreadyExistsException.class)
	public void testCreateDuplicatedResource() throws AlreadyExistsException {
		Collection<Resource> resources = client.getResources();
		assertFalse(resources.contains(RESOURCE_1));
		Long id = client.createResource(RESOURCE_1.getDescription(), RESOURCE_1.getInfo());
		RESOURCE_1.setId(id);
		assertTrue(client.getResources().contains(RESOURCE_1));
		// this should throw AlreadyExistsException:
		client.createResource(RESOURCE_1.getDescription(), "some info");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateResourceWithNullDescription() throws AlreadyExistsException {
		client.createResource(null, "some info");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateResourceWithEmptyDescription() throws AlreadyExistsException {
		client.createResource("", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateResourceWithBlankDescription() throws AlreadyExistsException {
		client.createResource("               ", null);
	}

	@Test(expected = NotFoundException.class)
	public void testDeleteResourceNotFound() throws NotFoundException {
		client.deleteResource(12345678L);
	}

	@Test
	public void testDeleteResource() throws AlreadyExistsException, NotFoundException {
		Long id = client.createResource(RESOURCE_1.getDescription(), RESOURCE_1.getInfo());
		RESOURCE_1.setId(id);
		assertTrue(client.getResources().contains(RESOURCE_1));
		client.deleteResource(id);
		assertFalse(client.getResources().contains(RESOURCE_1));
	}

	@Test(expected = NotFoundException.class)
	public void testGetEventNotFound() throws NotFoundException {
		client.getEvent(12345678L);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateEventWithInvalidDescription() {
		client.createEvent("", EVENT_1.getInfo(), EVENT_1.getInterval());
	}

	/*
	 * @Test public void testGetEvents() { ArrayList<Event> events = new
	 * ArrayList<Event>(client.getEvents());
	 * 
	 * assertEquals(EVENT_COUNT, events.size()); for (Event e : events) {
	 * logger.debug(e); } assertEquals(EXISTENT_EVENT, events.get(4));
	 * assertEquals(EVENT_COUNT, events.size());
	 * assertTrue(events.contains(EXISTENT_EVENT)); }
	 * 
	 * // FIXME: This test will fail until the #306 bug gets fixed.
	 * 
	 * @Test public void testGetEvent() { Event e; try { e =
	 * client.getEvent(EXISTENT_EVENT_ID); assertEquals(EXISTENT_EVENT, e); }
	 * catch (NotFoundException e1) { fail("get event"); } }
	 * 
	 * 
	 * @Test //@Ignore public void testCreateEvent() { String DESC =
	 * "new event desc"; String INFO = "new event info"; Interval INTERVAL = new
	 * Interval(new DateTime("2011-05-06T13:00:00"), new
	 * DateTime("2011-05-10T20:00:00")); Collection<Tag> TAGS = new
	 * TreeSet<Tag>(); //TAGS.add(EXISTENT_TAG);
	 * 
	 * Collection<Event> events = client.getEvents(); int eventCountBefore =
	 * events.size(); Event e = client.createEvent(DESC, INFO, INTERVAL, TAGS);
	 * logger.debug("Event creat: " + e.toString()); events =
	 * client.getEvents(); assertEquals(eventCountBefore + 1,
	 * client.getEvents().size()); assertTrue(events.contains(e)); }
	 * 
	 * @Test public void testUpdateEvent() throws NotFoundException { long
	 * EVENT_ID = 5L; String NOVADESC = "NovaDesc"; String NOVAINFO =
	 * "Nova informació"; Interval NOUINTERVAL = new Interval(new
	 * DateTime("2011-06-07T08:00:00"), new DateTime("2011-06-10T18:00:00"));
	 * Collection<Tag> NOVESTAGS = new HashSet<Tag>(); NOVESTAGS.add(new
	 * Tag("isabel", "descr 7")); NOVESTAGS.add(new Tag("wireless", "descr 8"));
	 * NOVESTAGS.add(new Tag("pantalla", "descr 2"));
	 * 
	 * Event e = client.getEvent(EVENT_ID); assertEquals(EXISTENT_EVENT, e);
	 * Event newEvent = new Event(NOVADESC, NOVAINFO, NOUINTERVAL, NOVESTAGS);
	 * Event updated = client.updateEvent(EVENT_ID, newEvent);
	 * assertEquals(NOVADESC, updated.getDescription()); assertEquals(NOVAINFO,
	 * updated.getInfo()); assertEquals(NOUINTERVAL, updated.getInterval());
	 * assertTrue(CollectionUtils.isEqualCollection(NOVESTAGS,
	 * updated.getTags())); }
	 * 
	 * @Test(expected = NotFoundException.class) public void
	 * testUpdateEventNotFound() throws NotFoundException { Event e = new
	 * Event("dummy desc", "dummy info", new Interval(new DateTime(), new
	 * DateTime()));
	 * 
	 * @SuppressWarnings("unused") Event updated =
	 * client.updateEvent(NON_EXISTENT_EVENT_ID, e); }
	 * 
	 * @Test(expected = NotFoundException.class) public void
	 * testDeleteEventNotFound() throws NotFoundException { // this should raise
	 * a NotFoundException client.deleteEvent(NON_EXISTENT_EVENT_ID); }
	 * 
	 * @Test public void testDeleteEvent() throws NotFoundException {
	 * Collection<Event> eventsBefore = client.getEvents();
	 * client.deleteEvent(EXISTENT_EVENT.getId()); Collection<Event> eventsAfter
	 * = client.getEvents(); assertEquals(eventsBefore.size()-1,
	 * eventsAfter.size()); assertFalse(eventsAfter.contains(EXISTENT_EVENT));
	 * EVENT_COUNT--; }
	 */
}
