package edu.upc.cpl.smeagol.client;

import java.util.Collection;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import edu.upc.cpl.smeagol.client.domain.Tag;
import edu.upc.cpl.smeagol.client.exception.NotFoundException;

/**
 * <code>SmeagolClient</code> tests.
 * <p>
 * WARNING: Before running these tests
 * 
 * @author angel
 * 
 */
public class SmeagolClientTest extends TestCase {

	private static Logger logger = Logger.getLogger(SmeagolClientTest.class);

	private static final String SERVER_URL = "http://localhost:3000";

	static {
		logger.info("****************************************************************************");
		logger.info(" NOTE: Before running these tests, check that a compatible Smeagol server   ");
		logger.info(" is running on " + SERVER_URL);
		logger.info("****************************************************************************");
	}

	private static SmeagolClient client;

	@Before
	public void setUp() throws Exception {
		client = new SmeagolClient(SERVER_URL);
	}

	@Test
	public void testGetTags() {
		Collection<Tag> tags = client.getTags();
		assertTrue(tags.size() > 0);
	}

	@Test
	public void testGetTag() {
		Tag t;
		try {
			t = client.getTag("videoconferencia");
			assertTrue(t != null);
			logger.debug(t.toString());
		} catch (NotFoundException e) {
			logger.error(e.getLocalizedMessage());
		}
	}

}
