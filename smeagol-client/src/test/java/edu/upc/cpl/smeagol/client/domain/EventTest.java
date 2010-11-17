package edu.upc.cpl.smeagol.client.domain;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

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

		E1_JSON = "{\"id\":" + ID1 + ",\"description\":\"" + DESC1
				+ "\",\"info\":\"" + INFO1 + "\",\"tags\":"
				+ Tag.serialize(TAG_LIST) + "}";

		e2 = new Event();
		e2.setId(ID2);
		e2.setDescription(DESC2);
		e2.setInfo(INFO2);
		e2.setTags(EMPTY_TAG_LIST);

		E2_JSON = "{\"id\":" + ID2 + ",\"description\":\"" + DESC2
				+ "\",\"info\":\"" + INFO2 + "\",\"tags\":"
				+ Tag.serialize(EMPTY_TAG_LIST) + "}";

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
	public void testSerialize() {
		assertEquals(E1_JSON, e1.serialize());
		assertEquals(E1_JSON, e1Copy.serialize());
		assertEquals(E2_JSON, e2.serialize());
	}

	@Test
	public void testDeserialize() {
		Event event1, event2;
		event1 = Event.deserialize(E1_JSON);
		assertEquals(e1, event1);
		event2 = Event.deserialize(E2_JSON);
		assertEquals(e2, event2);

	}

	@Test
	public void testDeserializeCollection() {
		String JSON_ARRAY = "[" + E1_JSON + "," + E2_JSON + "]";
		Collection<Event> events = Event.deserializeCollection(JSON_ARRAY);
		assertEquals(2, events.size());
		assertTrue(events.contains(e1));
		assertTrue(events.contains(e2));
	}

	@Test
	public void testSerializeCollection() {
		String JSON_ARRAY = "[" + E1_JSON + "," + E2_JSON + "]";
		Collection<Event> events = new ArrayList<Event>();
		events.add(e1);
		events.add(e2);
		assertEquals(JSON_ARRAY, Event.serialize(events));
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
