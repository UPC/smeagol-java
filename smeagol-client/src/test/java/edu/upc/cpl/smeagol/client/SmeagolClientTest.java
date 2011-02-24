package edu.upc.cpl.smeagol.client;

import java.net.MalformedURLException;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

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
	private static Tag EXISTENT_TAG = new Tag("videoconferencia", "descr 3");
	private static Tag NON_EXISTENT_TAG = new Tag("nonexistent", "non existent description");
	private static Tag PROJECTOR = new Tag("projector", "descr 1");
	private static String TAG_ID = "newtag";
	private static String TAG_DESCRIPTION = "new tag description";

	static {
		logger.info("**************************************************************************");
		logger.info("NOTE: Before running these tests, check that a compatible Smeagol server  ");
		logger.info("is running on " + SERVER_URL);
		logger.info("Anyway, you should use the file src/test/resources/db.sql to recreate the ");
		logger.info("server database before running the test suite.");
		logger.info("**************************************************************************");
	}

	private static SmeagolClient client;

	@Before
	public void setUp() throws Exception {
		client = new SmeagolClient(SERVER_URL);
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
			Tag t = client.createTag(NON_EXISTENT_TAG.getId(), NON_EXISTENT_TAG.getDescription());
			assertEquals(NON_EXISTENT_TAG, t);
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
		fail("Not yet implemented.");
	}

	public void testUpdateTagNotFound() {
		fail("Not yet implemented.");
	}
}
