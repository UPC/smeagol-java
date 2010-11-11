package edu.upc.cpl.smeagol.client.domain;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

public class SimpleBookingTest extends TestCase {

	private static Logger logger = Logger.getLogger(SimpleBookingTest.class);

	private static Integer ID_1 = 1;
	private static final DateTime START_1 = new DateTime(2010, 11, 1, 8, 0, 0, 0);
	private static final DateTime END_1 = new DateTime(2010, 11, 1, 10, 30, 0, 0);
	private static Integer ID_2 = 2;
	private static final DateTime START_2 = new DateTime(2010, 11, 1, 8, 0, 0, 0);
	private static final DateTime END_2 = new DateTime(2010, 11, 1, 10, 30, 0, 0);
	private static final Interval INTERVAL_1 = new Interval(START_1, END_1);
	private static final Interval INTERVAL_2 = new Interval(START_2, END_2);
	private SimpleBooking B1;
	private SimpleBooking B2;
	private String B1_AS_JSON = "{\"id\":" + ID_1 + ",\"start\":\"" + START_1.toString() + "\",\"end\":\"" + END_1
			+ "\"}";
	private String B2_AS_JSON = "{\"id\":" + ID_2 + ",\"start\":\"" + START_2.toString() + "\",\"end\":\"" + END_2
			+ "\"}";
	private String JSON_ARRAY = "[" + B1_AS_JSON + "," + B2_AS_JSON + "]";

	@Before
	public void setUp() throws Exception {
		B1 = new SimpleBooking(ID_1, INTERVAL_1);
		B2 = new SimpleBooking(ID_2, INTERVAL_2);
	}

	@Test
	public void testGetStart() {
		assertEquals(START_1, B1.getStart());
	}

	@Test
	public void testGetEnd() {
		assertEquals(END_1, B1.getEnd());
	}

	@Test
	public void testToJSONString() {
		assertEquals(B1_AS_JSON, B1.toJSONString());
	}

	@Test
	public void testFromJSONString() {
		try {
			Booking b = (new SimpleBooking()).fromJSONString(B1_AS_JSON);
			assertEquals(B1, b);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
	}

	@Test
	public void testCompareTo() {
		assertTrue(B1.compareTo(B1) == 0);
		assertTrue(B1.compareTo(B2) < 0);
		assertTrue(B2.compareTo(B1) > 0);
	}

	@Test
	public void testFromJSONArray() {
		Collection<Booking> bookings;
		try {
			bookings = (new SimpleBooking()).fromJSONArray(JSON_ARRAY);
			assertEquals(2, bookings.size());
			assertTrue(bookings.contains(B1));
			assertTrue(bookings.contains(B2));
		} catch (JSONException e) {
			logger.error(e.getLocalizedMessage());
		}
	}

	@Test
	public void testToJSONArray() {
		Collection<Booking> bookings = new ArrayList<Booking>();
		bookings.add(B1);
		bookings.add(B2);
		String json = (new SimpleBooking()).toJSONArray(bookings);
		assertEquals(JSON_ARRAY, json);
	}
}
