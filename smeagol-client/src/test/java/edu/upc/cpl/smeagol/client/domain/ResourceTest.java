package edu.upc.cpl.smeagol.client.domain;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ResourceTest extends TestCase {
	private static final long ID1 = 1;
	private static final long ID2 = 2;
	private static final String DESC1 = "description1";
	private static final String DESC2 = "description2";
	private static final String INFO1 = "chachi1";
	private static final String INFO2 = "chachi2";
	private String R1_AS_JSON;
	private String R2_AS_JSON;
	private String JSON_ARRAY;
	private Resource r1;
	private Resource r2;

	@Before
	public void setUp() throws Exception {
		r1 = new Resource(DESC1, INFO1);
		r2 = new Resource(DESC2, INFO2);
		r1.setId(ID1);
		r2.setId(ID2);

		R1_AS_JSON = "{\"id\":" + ID1 + ",\"description\":\"" + DESC1 + "\",\"info\":\"" + INFO1 + "\"}";
		R2_AS_JSON = "{\"id\":" + ID2 + ",\"description\":\"" + DESC2 + "\",\"info\":\"" + INFO2 + "\"}";
		JSON_ARRAY = "[" + R1_AS_JSON + "," + R2_AS_JSON + "]";
	}

	@Test
	public void testCompareTo() {
		assertTrue(r1.compareTo(r2) < 0);
		assertTrue(r2.compareTo(r1) > 0);
		assertTrue(r1.compareTo(r1) == 0);
	}

	@Test
	public void testEqualsObject() {
		assertFalse(r1.equals(r2));
		assertTrue(r1.equals(r1));

		Resource other = new Resource(DESC1, INFO1);
		other.setId(ID1);
		assertTrue(r1.equals(other));
	}

	@Test
	public void testSerialize() {
		assertEquals(R1_AS_JSON, r1.serialize());
	}

	@Test
	public void testDeserialize() {
		Resource r;
		r = Resource.deserialize(R1_AS_JSON);
		assertEquals(r1, r);
		r = Resource.deserialize(R2_AS_JSON);
		assertEquals(r2, r);
	}

	@Test
	public void testDeserializeCollection() {
		Collection<Resource> resources = Resource.deserializeCollection(JSON_ARRAY);
		assertEquals(2, resources.size());
		assertTrue(resources.contains(r1));
		assertTrue(resources.contains(r2));
	}

	@Test
	public void testSerializeCollection() {
		Collection<Resource> c = new ArrayList<Resource>();
		c.add(r1);
		c.add(r2);
		String jsonArray = Resource.serialize(c);
		assertEquals(JSON_ARRAY, jsonArray);
	}

}
