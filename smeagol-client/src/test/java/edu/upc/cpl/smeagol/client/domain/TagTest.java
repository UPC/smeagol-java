package edu.upc.cpl.smeagol.client.domain;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(JUnit4.class)
public class TagTest extends TestCase {

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(getClass());

	private static final String ID1 = "id1";
	private static final String ID2 = "id2";
	private static final String DESC1 = "description1";
	private static final String DESC2 = "description2";
	private static final String JSON1 = "{\"id\":\"" + ID1 + "\",\"description\":\"" + DESC1 + "\"}";
	private static final String JSON2 = "{\"id\":\"" + ID2 + "\",\"description\":\"" + DESC2 + "\"}";
	private static final String JSON_ARRAY = "[" + JSON1 + "," + JSON2 + "]";
	private Tag t1;
	private Tag t2;

	@Before
	public void setUp() throws Exception {
		t1 = new Tag(ID1, DESC1);
		t2 = new Tag(ID2, DESC2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorsWithIdTooShort() {
		String TOO_SHORT_ID = StringUtils.rightPad("a", Tag.ID_MIN_LEN - 1, "a");

		@SuppressWarnings("unused")
		Tag t = new Tag(TOO_SHORT_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorsWithIdTooLong() {
		String TOO_LONG_ID = StringUtils.rightPad("a", Tag.ID_MAX_LEN + 1, "a");

		@SuppressWarnings("unused")
		Tag t = new Tag(TOO_LONG_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorsWithDescTooLong() {
		String TOO_LONG_DESC = StringUtils.rightPad("a", Tag.DESCRIPTION_MAX_LEN + 1, "a");

		@SuppressWarnings("unused")
		Tag t = new Tag(TOO_LONG_DESC);
	}

	@Test
	public void testTagStringString() {
		assertEquals(ID1, t1.getId());
		assertEquals(DESC1, t1.getDescription());
	}

	@Test
	public void testCompareTo() {
		assertTrue(t1.compareTo(t2) < 0);
		assertTrue(t2.compareTo(t1) > 0);
	}

	@Test
	public void testEquals() {
		assertNotSame(t1, null);
		assertNotSame(t1, new String());
		assertEquals(t1, t1);
		assertNotSame(t1, t2);
		t2 = new Tag(ID1, DESC1);
		assertEquals(t1, t2);
	}

	@Test
	public void testSerialize() {
		JsonObject got = new JsonParser().parse(t1.serialize()).getAsJsonObject();
		JsonObject expected = new JsonParser().parse(JSON1).getAsJsonObject();
		assertTrue(got.has("id"));
		assertEquals(expected.get("id"), got.get("id"));
		assertTrue(got.has("description"));
		assertEquals(expected.get("description"), got.get("description"));
	}

	@Test
	public void testDeserialize() {
		String json = "{ \"id\" : \"" + ID1 + "\", \"description\" : \"" + DESC1 + "\"}";
		Tag t = Tag.deserialize(json);
		assertEquals(t1, t);
	}

	@Test
	public void testDeserializeCollection() {
		String jsonArr = "[" + JSON1 + "," + JSON2 + "]";
		Collection<Tag> t = Tag.deserializeCollection(jsonArr);
		assertEquals(2, t.size());
		assertTrue(t.contains(t1));
		assertTrue(t.contains(t2));
	}

	@Test
	public void testSerializeCollection() {
		Collection<Tag> tags = new ArrayList<Tag>();
		tags.add(t1);
		tags.add(t2);
		assertEquals(JSON_ARRAY, Tag.serialize(tags));
	}

}
