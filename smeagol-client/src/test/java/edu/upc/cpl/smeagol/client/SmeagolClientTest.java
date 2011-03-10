package edu.upc.cpl.smeagol.client;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

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
public class SmeagolClientTest extends TestCase {

	private static Logger logger = Logger.getLogger(SmeagolClientTest.class);

	/**
	 * url for test server
	 */
	private static final String SERVER_URL = "http://localhost:3000";

	private static int TAG_COUNT = 8; // number of tags in server
	private static int RESOURCE_COUNT = 5; // number of resources in server

	private static final Tag EXISTENT_TAG = new Tag("videoconferencia", "descr 3");
	private static final Tag NEW_TAG = new Tag("newtag",
			"description for a new tag with a really really really really really "
					+ "really really really really really really long description");
	private static final Tag NON_EXISTENT_TAG = new Tag("nonexistent", "non existent description");
	private static final Tag PROJECTOR = new Tag("projector", "descr 1");

	private static final Long EXISTENT_RESOURCE_ID = 2L;
	private static final Collection<Tag> EXISTENT_RESOURCE_TAGS = new TreeSet<Tag>();
	private static final Resource EXISTENT_RESOURCE = new Resource("Aula test 2", "Estem de proves");

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
	}

	@Test
	public void testConstructorWithMalformedUrl() {
		try {
			@SuppressWarnings("unused")
			SmeagolClient sc = new SmeagolClient("this is not a valid url");
			fail("testing constructor with malformed url");
		} catch (MalformedURLException e) {
			// OK (it should fail)
		}
	}

	@Test
	public void testGetTags() {
		Collection<Tag> tags = client.getTags();
		assertTrue(tags.size() == TAG_COUNT);
		assertTrue(tags.contains(EXISTENT_TAG));
	}

	@Test
	public void testGetTag() {
		Tag t;
		try {
			t = client.getTag(EXISTENT_TAG.getId());
			assertTrue(t != null);
			assertEquals(EXISTENT_TAG.getId(), t.getId());
			assertEquals(EXISTENT_TAG.getDescription(), t.getDescription());
		} catch (NotFoundException e) {
			logger.error(e.getLocalizedMessage());
		}
	}

	@Test
	public void testGetTagNotFound() {
		try {
			@SuppressWarnings("unused")
			Tag t = client.getTag(NON_EXISTENT_TAG.getId());
			fail("test getTag with non existent tag");
		} catch (NotFoundException e) {
			// OK. It should fail.
		}
	}

	@Test
	public void testCreateTagInvalidArgument() {
		try {
			@SuppressWarnings("unused")
			Tag t = client.createTag(null, null);
			fail("test create with null id and descriptions");
		} catch (IllegalArgumentException e) {
			// OK. It should fail.
		} catch (AlreadyExistsException e) {
			fail("test create with null id and descriptions");
		}
		assertEquals(TAG_COUNT, client.getTags().size());
	}

	@Test
	public void testCreateTagAlreadyExists() {
		try {
			@SuppressWarnings("unused")
			Tag t = client.createTag(EXISTENT_TAG.getId(), null);
			fail("test create existent tag");
		} catch (IllegalArgumentException e) {
			fail("test create existent tag");
		} catch (AlreadyExistsException e) {
			// OK. It should fail.
		}
		assertEquals(TAG_COUNT, client.getTags().size());
	}

	@Test
	public void testCreateTagWithNullDescription() {
		Tag aux = new Tag("aux", null);
		try {
			Tag t = client.createTag(aux.getId(), aux.getDescription());
			assertEquals(aux, t);
			assertEquals(TAG_COUNT + 1, client.getTags().size());
			TAG_COUNT++;
		} catch (IllegalArgumentException e) {
			fail("create tag with null description");
		} catch (AlreadyExistsException e) {
			fail("create tag with null description");
		}
	}

	@Test
	public void testCreateTag() {
		try {
			Tag t = client.createTag(NEW_TAG.getId(), NEW_TAG.getDescription());
			assertEquals(NEW_TAG, t);
			assertEquals(TAG_COUNT + 1, client.getTags().size());
			TAG_COUNT++;
		} catch (IllegalArgumentException e) {
			fail("create tag");
		} catch (AlreadyExistsException e) {
			fail("create tag");
		}
	}

	public void testDeleteTagNotFound() {
		try {
			client.deleteTag("TrulyInexistentTag");
			fail("delete non existent tag");
		} catch (NotFoundException e) {
			// nothing has been deleted
			assertEquals(TAG_COUNT, client.getTags().size());
		}
	}

	@Test
	public void testDeleteTag() {
		try {
			Collection<Tag> before = client.getTags();
			assertTrue(before.contains(PROJECTOR));
			client.deleteTag(PROJECTOR.getId());
			Collection<Tag> after = client.getTags();
			assertEquals(before.size() - 1, after.size());
			TAG_COUNT--;
			assertFalse(after.contains(PROJECTOR));
		} catch (NotFoundException e) {
			fail("delete tag");
		}
	}

	@Test
	public void testUpdateTag() {
		String OLD_DESCRIPTION = EXISTENT_TAG.getDescription();
		String NEW_DESCRIPTION = "hi! am a new description";
		try {
			Collection<Tag> tags = client.getTags();
			assertTrue(tags.contains(EXISTENT_TAG));
			client.updateTag(EXISTENT_TAG.getId(), NEW_DESCRIPTION);
			Tag updated = client.getTag(EXISTENT_TAG.getId());
			assertEquals(EXISTENT_TAG.getId(), updated.getId());
			assertEquals(NEW_DESCRIPTION, updated.getDescription());
			client.updateTag(EXISTENT_TAG.getId(), OLD_DESCRIPTION);
			Tag old = client.getTag(EXISTENT_TAG.getId());
			assertEquals(EXISTENT_TAG.getDescription(), old.getDescription());
		} catch (NotFoundException e) {
			fail("update tag");
		}
	}

	public void testUpdateTagNotFound() {
		Collection<Tag> tags = client.getTags();
		assertFalse(tags.contains(NON_EXISTENT_TAG));
		try {
			client.updateTag("dummy100", "dummy value");
			fail("update tag not found");
		} catch (NotFoundException e) {
			// OK. It should fail.
		}
	}

	public void testGetResources() {
		Collection<Resource> resources = client.getResources();
		assertEquals(RESOURCE_COUNT, resources.size());
		assertTrue(resources.contains(EXISTENT_RESOURCE));
	}
}
