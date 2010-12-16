package edu.upc.cpl.smeagol.client.domain;

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;

import edu.upc.cpl.smeagol.client.ical.DayOfWeek;
import edu.upc.cpl.smeagol.client.ical.Frequency;

public class RecurrentBookingTest {

	/*
	 * Booking 1: booking for resource 1 and event 10. Every monday from 08:00
	 * to 09:00 starting on 2011-01-01, until 2011-01-31.
	 */
	public static long ID_RESOURCE1 = 1;
	public static long ID_EVENT1 = 10;
	public static DateTime DTSTART1 = new DateTime("2011-01-01T00:00:00");
	public static Duration DURATION1 = Duration.standardMinutes(60);
	public static Frequency FREQUENCY1 = Frequency.WEEKLY;
	public static Set<DayOfWeek> BY_DAY1 = new HashSet<DayOfWeek>();
	
	public static long ID_RESOURCE2 = 2;
	public static long ID_EVENT2 = 20;
	public static DateTime DTSTART2 = new DateTime("2011-01-02T15:00:00");
	public static int DURATION2 = 60;
	public static RecurrentBooking B1;
	public static RecurrentBooking B2;

	@Before
	public void setUp() throws Exception {
		B1 = new RecurrentBooking();
		B1.setIdResource(ID_RESOURCE1);
		B1.setIdEvent(ID_EVENT1);
		B1.setDtStart(DTSTART1);
		B1.setDuration(DURATION1);
		B1.setFrequency(FREQUENCY1);
		BY_DAY1.add(DayOfWeek.MONDAY);
		B1.setByDay(BY_DAY1);

		B2 = new RecurrentBooking();
	}

	@Test
	public void testSerialize() {
		fail("Not yet implemented");
	}

	@Test
	public void testSerializeCollection() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeserialize() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeserializeCollection() {
		fail("Not yet implemented");
	}

	@Test
	public void testCompareTo() {
		fail("Not yet implemented");
	}

	@Test
	public void testEqualsObject() {
		fail("Not yet implemented");
	}

}
