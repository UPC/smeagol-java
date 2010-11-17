package edu.upc.cpl.smeagol.client;

import java.util.Collection;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import edu.upc.cpl.smeagol.client.domain.Tag;

public class SmeagolClientTest extends TestCase {

	private static Logger logger = Logger.getLogger(SmeagolClientTest.class);

	private static SmeagolClient client;

	@Before
	public void setUp() throws Exception {
		client = new SmeagolClient("http://abydos.ac.upc.edu:3000");
	}

	@Test
	public void testGetTags() {
		Collection<Tag> tags = client.getTags();
		assertTrue(tags.size() > 0);
		for (Tag t : tags) {
			logger.debug(t.toString());
		}
	}

	@Test
	public void testGetTag() {
		Tag t = client.getTag("isabel");
		assertTrue(t != null);
		logger.debug(t.toString());
	}

}
