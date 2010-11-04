package edu.upc.cpl.smeagol.client.domain;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class EventTest extends TestCase {

	private static final String ID1 = "id1";
	private static final String ID2 = "id2";
	private static final String DESC1 = "description1";
	private static final String DESC2 = "description2";
	private static final String INFO1 = "info1";
	private static final String INFO2 = "info2";
	private static final DateTime STARTS1 = new DateTime();
	private static final DateTime ENDS1 = new DateTime();
	private static final Collection<Tag> EMPTY_TAG_LIST = new ArrayList<Tag>();
	private static Collection<Tag> TAG_LIST;
	private static final Tag T1 = new Tag("tagId1", "tagDesc1");
	private static final Tag T2 = new Tag("tagId2", "tagDesc2");

	@Before
	@Override
	public void setUp() throws Exception {
	}

	@Test
	public void testCompareTo() {
		fail("Not yet implemented");
	}

	@Test
	public void testEqualsObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testToJSONString() {
		fail("Not yet implemented");
	}

	@Test
	public void testFromJSONString() {
		fail("Not yet implemented");
	}

	@Test
	public void testFromJSONArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testToJSONArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStarts() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetEnds() {
		fail("Not yet implemented");
	}

}
