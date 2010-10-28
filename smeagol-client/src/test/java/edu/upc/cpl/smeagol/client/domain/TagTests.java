package edu.upc.cpl.smeagol.client.domain;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import edu.upc.cpl.smeagol.client.domain.Tag;

public class TagTests extends TestCase {

	private Logger	logger	= Logger.getLogger(getClass());

	private String	ID1		= "id1";
	private String	ID2		= "id2";
	private String	DESC1	= "description1";
	private String	DESC2	= "description2";
	private Tag		t1;
	private Tag		t2;

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

	public void testToString() {
		// TODO: build a real test for toString()
		logger.debug(t1.toString());
	}

	public void testToJSON() {
		// TODO: build a real test for testToJSON()
		logger.debug(t1.toJSON());
	}

}

