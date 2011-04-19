package edu.upc.cpl.smeagol.client.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class EventTest extends TestCase {

	private static final long ID1 = 1;
	private static final long ID2 = 2;
	private static final String DESC1 = "description1";
	private static final String DESC2 = "description2";
	private static final String INFO1 = "info1";
	private static final String INFO2 = "info2";
	private static final Tag TAG1 = new Tag("tag1", "desc1");
	private static final Tag TAG2 = new Tag("tag2", "desc2");
	private static final Tag TAG3 = new Tag("tag3", "desc3");
	private static final DateTime STARTS1 = new DateTime("2010-12-01T08:00:00");
	private static final DateTime ENDS1 = new DateTime("2010-12-10T19:00:00");
	private static final DateTime STARTS2 = new DateTime("2011-01-01T08:00:00");
	private static final DateTime ENDS2 = new DateTime("2011-01-31T19:00:00");
	private Event e1;
	private Event e2;
	private Event e1Copy;
	private String E1_JSON;
	private String E2_JSON;

	@Before
	public void setUp() throws Exception {
		e1 = new Event();
		e1.setId(ID1);
		e1.setDescription(DESC1);
		e1.setInfo(INFO1);
		e1.setInterval(new Interval(STARTS1, ENDS1));
		e1.setTags(Arrays.asList(new Tag[] { TAG1, TAG2, TAG3 }));

		E1_JSON = "{\"id\":" + e1.getId() + ",\"description\":\"" + e1.getDescription() + "\",\"info\":\""
				+ e1.getInfo() + "\",\"starts\":\"" + e1.getInterval().getStart() + "\",\"ends\":\""
				+ e1.getInterval().getEnd() + "\",\"tags\":" + Tag.serialize(e1.getTags()) + "}";

		e2 = new Event();
		e2.setId(ID2);
		e2.setDescription(DESC2);
		e2.setInfo(INFO2);
		e2.setInterval(new Interval(STARTS2, ENDS2));
		e2.setTags(new ArrayList<Tag>());

		E2_JSON = "{\"id\":" + e2.getId() + ",\"description\":\"" + e2.getDescription() + "\",\"info\":\""
				+ e2.getInfo() + "\",\"starts\":\"" + e2.getInterval().getStart() + "\",\"ends\":\""
				+ e2.getInterval().getEnd() + "\",\"tags\":" + Tag.serialize(e2.getTags()) + "}";

		/*
		 * e1 and e1Copy will have the same attributes and tags, so
		 * e1.equals(e1Copy) should return true
		 */
		e1Copy = new Event();
		e1Copy.setId(ID1);
		e1Copy.setDescription(DESC1);
		e1Copy.setInfo(INFO1);
		e1Copy.setInterval(new Interval(STARTS1, ENDS1));
		e1Copy.setTags(Arrays.asList(new Tag[] { TAG1, TAG2, TAG3 }));
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
	public void tesGetInterval() {
		assertEquals(new Interval(STARTS1, ENDS1), e1.getInterval());
	}

}
