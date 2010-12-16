package edu.upc.cpl.smeagol.client.domain;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

public class ResourceTest extends TestCase {
	private static final long ID1 = 1;
	private static final long ID2 = 2;
	private static final String DESC1 = "description1";
	private static final String DESC2 = "description2";
	private static final String INFO1 = "chachi1";
	private static final String INFO2 = "chachi2";
	private static final Tag T1 = new Tag("idtag1", "desctag1");
	private static final Tag T2 = new Tag("idtag2", "desctag2");
	private static final Collection<Tag> EMPTY_TAG_LIST = new ArrayList<Tag>();
	private String R1_AS_JSON;
	private String R2_AS_JSON;
	private String JSON_ARRAY;
	private Resource r1;
	private Resource r2;

	protected void setUp() throws Exception {
		r1 = new Resource();
		r2 = new Resource();
		r1.setId(ID1);
		r2.setId(ID2);
		r1.setDescription(DESC1);
		r2.setDescription(DESC2);
		r1.setInfo(INFO1);
		r2.setInfo(INFO2);
		Collection<Tag> tags = new ArrayList<Tag>();
		tags.add(T1);
		tags.add(T2);
		r1.setTags(tags);
		r2.setTags(EMPTY_TAG_LIST);
		R1_AS_JSON = "{\"id\":" + ID1 + ",\"description\":\"" + DESC1
				+ "\",\"info\":\"" + INFO1 + "\",\"tags\":"
				+ Tag.serialize(tags) + "}";
		R2_AS_JSON = "{\"id\":" + ID2 + ",\"description\":\"" + DESC2
				+ "\",\"info\":\"" + INFO2 + "\",\"tags\":"
				+ Tag.serialize(EMPTY_TAG_LIST) + "}";
		JSON_ARRAY = "[" + R1_AS_JSON + "," + R2_AS_JSON + "]";
	}

	public void testCompareTo() {
		assertTrue(r1.compareTo(r2) < 0);
		assertTrue(r2.compareTo(r1) > 0);
		assertTrue(r1.compareTo(r1) == 0);
	}

	public void testEqualsObject() {
		assertFalse(r1.equals(r2));
		assertTrue(r1.equals(r1));
		Resource other = new Resource();
		other.setId(ID1);
		other.setDescription(DESC1);
		other.setInfo(INFO1);
		Collection<Tag> c = new ArrayList<Tag>();
		c.addAll(r1.getTags());
		other.setTags(c);
		assertTrue(r1.equals(other));
		assertEquals(r1.getTags(), other.getTags());
	}

	public void testSerialize() {
		assertEquals(R1_AS_JSON, r1.serialize());
	}

	public void testDeserialize() {
		Resource r;
		r = Resource.deserialize(R1_AS_JSON);
		assertEquals(r1, r);
		r = Resource.deserialize(R2_AS_JSON);
		assertEquals(r2, r);
	}

	public void testDeserializeCollection() {
		Collection<Resource> resources = Resource
				.deserializeCollection(JSON_ARRAY);
		assertEquals(2, resources.size());
		assertTrue(resources.contains(r1));
		assertTrue(resources.contains(r2));
	}

	public void testSerializeCollection() {
		Collection<Resource> c = new ArrayList<Resource>();
		c.add(r1);
		c.add(r2);
		String jsonArray = Resource.serialize(c);
		assertEquals(JSON_ARRAY, jsonArray);
	}

}
