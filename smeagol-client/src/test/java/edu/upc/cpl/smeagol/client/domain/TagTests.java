package edu.upc.cpl.smeagol.client.domain;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.json.JSONException;

public class TagTests extends TestCase {

	private Logger logger = Logger.getLogger(getClass());

	private static final String ID1 = "id1";
	private static final String ID2 = "id2";
	private static final String DESC1 = "description1";
	private static final String DESC2 = "description2";
	private static final String JSON1 = "{\"id\":\""+ID1+"\",\"description\":\""+DESC1+"\"}";
	private static final String JSON2 = "{\"id\":\""+ID2+"\",\"description\":\""+DESC2+"\"}";
	private Tag t1;
	private Tag t2;

	@Override
	protected void setUp() throws Exception {
		t1 = new Tag(ID1, DESC1);
		t2 = new Tag(ID2, DESC2);
	}

	public void testTagStringString() {
		assertEquals(ID1, t1.getId());
		assertEquals(DESC1, t1.getDescription());
	}

	public void testCompareTo() {
		assertTrue(t1.compareTo(t2) < 0);
		assertTrue(t2.compareTo(t1) > 0);
	}

	public void testEquals() {
		assertNotSame(t1, null);
		assertNotSame(t1, new String());
		assertEquals(t1, t1);
		assertNotSame(t1, t2);
		t2 = new Tag(ID1, DESC1);
		assertEquals(t1, t2);
	}

	public void testToJSON() {
		try {
			assertEquals(JSON1, t1.toJSON());
		} catch (JSONException e1) {
			fail(e1.getLocalizedMessage());
		}
	}

	public void testFromJSON() {
		String json = "{ \"id\" : \"" + ID1 + "\", \"description\" : \""
				+ DESC1 + "\"}";
		try {
			Tag t = Tag.fromJSON(json);
			assertEquals(t1, t);
		} catch (JSONException e) {
			fail(e.getLocalizedMessage());
		}
	}

	public void testFromJSONArray() {
		String jsonArr = "[" + JSON1 + "," + JSON2 + "]";
		try {
			Collection<Tag> t = Tag.fromJSONArray(jsonArr);
			assertTrue(t.contains(t1));
			assertTrue(t.contains(t2));
			assertEquals(2, t.size());
		} catch (JSONException e) { 
			fail(e.getLocalizedMessage());
		}
	}
	
	public void testToJSONArray() {
		Collection<Tag> tags = new ArrayList<Tag>();
		tags.add(t1);
		tags.add(t2);
		try {
			logger.debug(Tag.toJSONArray(tags));
		} catch (JSONException e) {
			logger.error(e.getLocalizedMessage());
		}
	}

}
