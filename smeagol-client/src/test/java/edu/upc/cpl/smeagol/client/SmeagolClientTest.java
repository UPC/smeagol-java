package edu.upc.cpl.smeagol.client;

import java.net.MalformedURLException;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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

	private static final Tag TAG_WITH_NULL_DESCRIPTION = new Tag("tag", null);
	private static final Tag TAG_1 = new Tag("tag1", "tag 1 description");
	private static final Tag TAG_2 = new Tag("tag2", "tag 2 description");

	// private static final Long EXISTENT_RESOURCE_ID = 2L;
	// private static final Collection<Tag> EXISTENT_RESOURCE_TAGS = new
	// TreeSet<Tag>();
	// private static final Resource EXISTENT_RESOURCE = new
	// Resource("Aula test 2", "Estem de proves");
	// private static final Resource NEW_RESOURCE = new
	// Resource("New resource desc", "New resource info");
	// private static final Collection<Tag> NEW_RESOURCE_TAGS = new
	// TreeSet<Tag>();
	// private static final Long NON_EXISTENT_RESOURCE_ID = 2000L;
	// private static final Resource NON_EXISTENT_RESOURCE = new
	// Resource("NON EXISTENT RESOURCE",
	// "NON EXISTENT DESCRIPTION");

	// private static final long EXISTENT_EVENT_ID = 5L;
	// private static Event EXISTENT_EVENT = new
	// Event("Descripcio de l'event 5", "Informacio 5", new Interval(
	// new DateTime("2011-04-20T08:00:00"), new
	// DateTime("2011-04-25T14:00:00")));
	// private static Collection<Tag> EXISTENT_EVENT_TAGS = new TreeSet<Tag>();
	// private static final long NON_EXISTENT_EVENT_ID = 4000L;
	// private static final Event NON_EXISTENT_EVENT = new
	// Event("NON EXISTENT EVENT", "NON EXISTENT EVENT INFO",
	// new Interval(new DateTime("2000-01-01T08:00:00"), new
	// DateTime("2000-01-01T10:00:00")),
	// new ArrayList<Tag>());

	static {
		logger.info("*******************************************************************************");
		logger.info("NOTE: Before running these tests, check that a compatible Smeagol server       ");
		logger.info("with an empty database is running on " + SERVER_URL);
		logger.info("WARNING: ALL INFORMATION IN THE DATABASE WILL BE LOST                          ");
		logger.info("*******************************************************************************");
	}

	private static SmeagolClient client;

	@Before
	public void setUp() throws Exception {
		client = new SmeagolClient(SERVER_URL);

		// EXISTENT_RESOURCE.setId(EXISTENT_RESOURCE_ID);
		// EXISTENT_RESOURCE_TAGS.clear();
		// EXISTENT_RESOURCE_TAGS.add(new Tag("pantalla", "descr 2"));
		// EXISTENT_RESOURCE_TAGS.add(new Tag("wireless", "descr 8"));

		// NEW_RESOURCE_TAGS.clear();
		// NEW_RESOURCE_TAGS.add(new Tag("isabel", "descr 7"));
		// NEW_RESOURCE_TAGS.add(new Tag("wireless", "descr 8"));

		// NON_EXISTENT_RESOURCE.setId(NON_EXISTENT_RESOURCE_ID);

		// EXISTENT_EVENT.setId(EXISTENT_EVENT_ID);
		// EXISTENT_EVENT_TAGS.clear();
		// EXISTENT_EVENT_TAGS.add(new Tag("isabel", "descr 7"));
		// EXISTENT_EVENT_TAGS.add(new Tag("videoconferencia", "descr 3"));
		// EXISTENT_EVENT_TAGS.add(new Tag("microfons inalambrics", "descr 6"));
		// EXISTENT_EVENT_TAGS.add(new Tag("wireless", "descr 8"));

		// EXISTENT_EVENT.setTags(EXISTENT_EVENT_TAGS);
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
		client.createTag(aux.getId(), aux.getDescription());
		Tag g = client.getTag(TAG_WITH_NULL_DESCRIPTION.getId());
		assertEquals(aux, g);
		assertEquals(tagsInServer + 1, client.getTags().size());
	}

	@Test
	public void testCreateTag() throws AlreadyExistsException, NotFoundException {
		int tagsInServer = client.getTags().size();
		client.createTag(TAG_1.getId(), TAG_1.getDescription());
		assertEquals(tagsInServer + 1, client.getTags().size());
		assertEquals(TAG_1, client.getTag(TAG_1.getId()));
	}

	@Test(expected = AlreadyExistsException.class)
	public void testCreateTagAlreadyExists() throws AlreadyExistsException {
		client.createTag(TAG_1.getId(), null);
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
	public void testUpdateTag() throws NotFoundException {
		String OLD_DESCRIPTION = TAG_1.getDescription();
		String NEW_DESCRIPTION = "hi! am a new description á é";
		Collection<Tag> tags = client.getTags();

		assertTrue(tags.contains(TAG_1));
		client.updateTag(TAG_1.getId(), NEW_DESCRIPTION);
		Tag updated = client.getTag(TAG_1.getId());
		assertEquals(TAG_1.getId(), updated.getId());
		assertEquals(NEW_DESCRIPTION, updated.getDescription());
		client.updateTag(TAG_1.getId(), OLD_DESCRIPTION);
		Tag old = client.getTag(TAG_1.getId());
		assertEquals(TAG_1.getDescription(), old.getDescription());
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
	public void testDeleteTag() throws NotFoundException {
		Collection<Tag> before = client.getTags();
		assertTrue(before.contains(TAG_1));
		client.deleteTag(TAG_1.getId());
		Collection<Tag> after = client.getTags();
		assertEquals(before.size() - 1, after.size());
		assertFalse(after.contains(TAG_1));
	}

	/*
	 * @Test public void testGetResources() { Collection<Resource> resources =
	 * client.getResources(); assertEquals(RESOURCE_COUNT, resources.size());
	 * assertTrue(resources.contains(EXISTENT_RESOURCE)); }
	 * 
	 * @Test(expected = NotFoundException.class) public void
	 * testGetResourceNotFound() throws NotFoundException {
	 * 
	 * @SuppressWarnings("unused") Resource r =
	 * client.getResource(NON_EXISTENT_RESOURCE.getId()); }
	 * 
	 * @Test public void testGetResource() throws NotFoundException { Resource r
	 * = client.getResource(EXISTENT_RESOURCE.getId()); assertNotNull(r);
	 * assertEquals(EXISTENT_RESOURCE, r); }
	 * 
	 * @Test(expected = AlreadyExistsException.class) public void
	 * testCreateDuplicatedResource() throws AlreadyExistsException,
	 * IllegalArgumentException { Collection<Resource> resources =
	 * client.getResources(); assertTrue(resources.contains(EXISTENT_RESOURCE));
	 * 
	 * @SuppressWarnings("unused") Resource r =
	 * client.createResource(EXISTENT_RESOURCE.getDescription(), null); }
	 * 
	 * @Test(expected = IllegalArgumentException.class) public void
	 * testCreateResourceWithNullDescription() throws AlreadyExistsException,
	 * IllegalArgumentException { String NULL_STR = null;
	 * 
	 * @SuppressWarnings("unused") Resource r = client.createResource(NULL_STR,
	 * null); }
	 * 
	 * @Test(expected = IllegalArgumentException.class) public void
	 * testCreateResourceWithEmptyDescription() throws AlreadyExistsException,
	 * IllegalArgumentException { String EMPTY_STR = "";
	 * 
	 * @SuppressWarnings("unused") Resource r = client.createResource(EMPTY_STR,
	 * null); }
	 * 
	 * @Test(expected = IllegalArgumentException.class) public void
	 * testCreateResourceWithBlankDescription() throws AlreadyExistsException,
	 * IllegalArgumentException { String BLANKS_STR = "         ";
	 * 
	 * @SuppressWarnings("unused") Resource r =
	 * client.createResource(BLANKS_STR, null); }
	 * 
	 * @Test public void testCreateResource() throws AlreadyExistsException,
	 * IllegalArgumentException { Collection<Resource> resources =
	 * client.getResources(); assertEquals(RESOURCE_COUNT, resources.size());
	 * assertFalse(resources.contains(NEW_RESOURCE)); Resource r =
	 * client.createResource(NEW_RESOURCE.getDescription(),
	 * NEW_RESOURCE.getInfo()); resources = client.getResources();
	 * NEW_RESOURCE.setId(r.getId()); assertEquals(NEW_RESOURCE, r);
	 * assertEquals(++RESOURCE_COUNT, resources.size());
	 * assertTrue(resources.contains(NEW_RESOURCE)); }
	 * 
	 * @Test(expected = NotFoundException.class) public void
	 * testDeleteResourceNotFound() throws NotFoundException {
	 * Collection<Resource> resources = client.getResources();
	 * assertEquals(RESOURCE_COUNT, resources.size());
	 * client.deleteResource(NON_EXISTENT_RESOURCE_ID); }
	 * 
	 * @Test public void testDeleteResource() { Collection<Resource> resources =
	 * client.getResources(); assertEquals(RESOURCE_COUNT, resources.size());
	 * try { client.deleteResource(EXISTENT_RESOURCE_ID); } catch
	 * (NotFoundException e) { fail("delete resource"); } Collection<Resource>
	 * resourcesAfter = client.getResources();
	 * assertFalse(resourcesAfter.contains(EXISTENT_RESOURCE));
	 * assertEquals(--RESOURCE_COUNT, resourcesAfter.size()); }
	 * 
	 * 
	 * // FIXME: This test will fail until the #306 bug gets fixed.
	 * 
	 * @Test public void testGetEvents() { ArrayList<Event> events = new
	 * ArrayList<Event>(client.getEvents());
	 * 
	 * assertEquals(EVENT_COUNT, events.size()); for (Event e : events) {
	 * logger.debug(e); } assertEquals(EXISTENT_EVENT, events.get(4));
	 * assertEquals(EVENT_COUNT, events.size());
	 * assertTrue(events.contains(EXISTENT_EVENT)); }
	 * 
	 * @Test(expected = NotFoundException.class) public void
	 * testGetEventNotFound() throws NotFoundException {
	 * 
	 * @SuppressWarnings("unused") Event event =
	 * client.getEvent(NON_EXISTENT_EVENT_ID); }
	 * 
	 * 
	 * // FIXME: This test will fail until the #306 bug gets fixed.
	 * 
	 * @Test public void testGetEvent() { Event e; try { e =
	 * client.getEvent(EXISTENT_EVENT_ID); assertEquals(EXISTENT_EVENT, e); }
	 * catch (NotFoundException e1) { fail("get event"); } }
	 * 
	 * @Test public void testCreateEventWithInvalidDescription() throws
	 * AlreadyExistsException { String NULL_DESCRIPTION = null; String
	 * EMPTY_DESCRIPTION = ""; String BLANK_DESCRIPTION = "   ";
	 * 
	 * @SuppressWarnings("unused") Event evt;
	 * 
	 * try { evt = client.createEvent(NULL_DESCRIPTION, null,
	 * EXISTENT_EVENT.getInterval(), null); EVENT_COUNT++;
	 * fail("create event with null description"); } catch
	 * (IllegalArgumentException e) { // ok. it should fail }
	 * 
	 * try { evt = client.createEvent(EMPTY_DESCRIPTION, null,
	 * EXISTENT_EVENT.getInterval(), null); EVENT_COUNT++;
	 * fail("create event with empty description"); } catch (Exception e) { //
	 * ok. it should fail }
	 * 
	 * try { evt = client.createEvent(BLANK_DESCRIPTION, null,
	 * EXISTENT_EVENT.getInterval(), null); EVENT_COUNT++;
	 * fail("create event with blank description");
	 * 
	 * } catch (Exception e) { // ok. it should fail } }
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
