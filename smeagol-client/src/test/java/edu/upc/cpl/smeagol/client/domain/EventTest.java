package edu.upc.cpl.smeagol.client.domain;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONStringer;
import org.junit.Before;
import org.junit.Test;

public class EventTest extends TestCase {

	private static final int ID1 = 1;
	private static final int ID2 = 2;
	private static final String DESC1 = "description1";
	private static final String DESC2 = "description2";
	private static final String INFO1 = "info1";
	private static final String INFO2 = "info2";
	private static final Collection<Tag> EMPTY_TAG_LIST = new ArrayList<Tag>();
	private static Collection<Tag> TAG_LIST;
	private static final Tag T1 = new Tag("tagId1", "tagDesc1");
	private static final Tag T2 = new Tag("tagId2", "tagDesc2");
	private Event e1;
	private Event e2;
	private Event e1Copy;
	private String E1_JSON;
	private String E2_JSON;

	@Before
	@Override
	public void setUp() throws Exception {
		e1 = new Event();
		e1.setId(ID1);
		e1.setDescription(DESC1);
		e1.setInfo(INFO1);
		TAG_LIST = new ArrayList<Tag>();
		TAG_LIST.add(T1);
		TAG_LIST.add(T2);
		e1.setTags(TAG_LIST);

		E1_JSON = new JSONStringer().object().key("id").value(ID1)
				.key("description").value(DESC1).key("info").value(INFO1)
				.key("tags").array().value(T1).value(T2).endArray().endObject()
				.toString();

		e2 = new Event();
		e2.setId(ID2);
		e2.setDescription(DESC2);
		e2.setInfo(INFO2);
		e2.setTags(EMPTY_TAG_LIST);

		E2_JSON = new JSONStringer().object().key("id").value(ID2)
				.key("description").value(DESC2).key("info").value(INFO2)
				.key("tags").array().endArray().endObject().toString();

		/*
		 * e1 and e1Copy will have the same attributes and tags, so
		 * e1.equals(e1Copy) should return true
		 */
		e1Copy = new Event();
		e1Copy.setId(ID1);
		e1Copy.setDescription(DESC1);
		e1Copy.setInfo(INFO1);
		Collection<Tag> tags = new ArrayList<Tag>();
		tags.add(new Tag("tagId1", "tagDesc1"));
		tags.add(new Tag("tagId2", "tagDesc2"));
		e1Copy.setTags(tags);
	}

	@Test
	public void testCompareTo() {
		assertEquals(0, e1.compareTo(e1));
		assertEquals(0, e1.compareTo(e1Copy));
		assertTrue(e1.compareTo(e2) < 0);
		assertTrue(e2.compareTo(e1) > 0);
	}

	@Test
	public void testEqualsObject() {
		assertTrue(e1.equals(e1));
		assertTrue(e1.equals(e1Copy));
		assertFalse(e1.equals(e2));
	}

	@Test
	public void testToJSONString() {
		assertEquals(E1_JSON, e1.toJSONString());
		assertEquals(E1_JSON, e1Copy.toJSONString());
		assertEquals(E2_JSON, e2.toJSONString());
	}

	@Test
	public void testFromJSONString() {
		Event event1, event2;
		try {
			event1 = Event.fromJSONString(E1_JSON);
			assertEquals(e1, event1);
			event2 = Event.fromJSONString(E2_JSON);
			assertEquals(e2, event2);
		} catch (JSONException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testFromJSONArray() {
		String JSON_ARRAY = "[" + E1_JSON + "," + E2_JSON + "]";
		try {
			Collection<Event> events = Event.fromJSONArray(JSON_ARRAY);
			assertEquals(2, events.size());
			assertTrue(events.contains(e1));
			assertTrue(events.contains(e2));
		} catch (JSONException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testToJSONArray() {
		String JSON_ARRAY = "[" + E1_JSON + "," + E2_JSON + "]";
		Collection<Event> events = new ArrayList<Event>();
		events.add(e1);
		events.add(e2);
		assertEquals(JSON_ARRAY, Event.toJSONArray(events));
	}

	@Test
	public void testGetStarts() {
		// TODO Auto-generated method stub
		fail("Not yet implemented");
	}

	@Test
	public void testGetEnds() {
		// TODO Auto-generated method stub
		fail("Not yet implemented");
	}

}
