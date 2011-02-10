package edu.upc.cpl.smeagol.client.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Before;
import org.junit.Test;

import edu.upc.cpl.smeagol.client.ical.DayOfWeek;
import edu.upc.cpl.smeagol.client.ical.Frequency;

public class RecurrentBookingTest extends TestCase {

	private static long ID_RESOURCE1 = 111;
	private static long ID_EVENT1 = 222;
	private static DateTime DTSTART1 = new DateTime(2011, 2, 3, 8, 0, 0, 0);
	private static DateTime DTEND1 = new DateTime(2011, 2, 3, 10, 30, 0, 0);
	private static short INTERVAL1 = 2;
	private static DateTime UNTIL1 = new DateTime(2011, 7, 9, 0, 0, 0, 0);
	private static Frequency FREQUENCY1 = Frequency.WEEKLY;
	private static Set<DayOfWeek> BY_DAY1 = new HashSet<DayOfWeek>();
	private static Set<Short> BY_DAYOFMONTH1 = new TreeSet<Short>();
	private static Set<Short> BY_MONTH1 = new TreeSet<Short>();

	private static long ID_RESOURCE2 = 2;
	private static long ID_EVENT2 = 20;
	private static DateTime DTSTART2 = new DateTime("2011-01-02T15:00:00");
	private static RecurrentBooking B1;
	private static RecurrentBooking B2;

	@Before
	@Override
	public void setUp() throws Exception {
		B1 = new RecurrentBooking();
		B1.setIdResource(ID_RESOURCE1);
		B1.setIdEvent(ID_EVENT1);
		B1.setDtStart(DTSTART1);
		B1.setFrequency(FREQUENCY1);
		BY_DAY1.add(DayOfWeek.MONDAY);
		BY_DAY1.add(DayOfWeek.WEDNESDAY);
		B1.setByDay(BY_DAY1);
		BY_DAYOFMONTH1.add((short) 10);
		BY_DAYOFMONTH1.add((short) 15);
		BY_DAYOFMONTH1.add((short) -1);
		BY_MONTH1.add((short) DateTimeConstants.JANUARY);
		BY_MONTH1.add((short) DateTimeConstants.MARCH);
		BY_MONTH1.add((short) DateTimeConstants.JULY);

		B2 = new RecurrentBooking();
	}

	@Test
	public void testAsDailyRecurrence() {
		RecurrentBooking r = RecurrentBooking.asDailyRecurrence(ID_RESOURCE1, ID_EVENT1, DTSTART1, DTEND1, INTERVAL1,
				UNTIL1);
		assertEquals(Frequency.DAILY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource().longValue());
		assertEquals(ID_EVENT1, r.getIdEvent().longValue());
		assertNotNull(r.getDtStart());
		assertEquals(DTEND1, r.getDtEnd());
		assertEquals(INTERVAL1, r.getInterval().shortValue());
		assertEquals(UNTIL1, r.getUntil());
	}

	@Test
	public void testAsDailyRecurrenceWithDefaultArgs() {
		RecurrentBooking r = RecurrentBooking.asDailyRecurrence(ID_RESOURCE1, ID_EVENT1, null, null, null, null);
		assertEquals(Frequency.DAILY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource().longValue());
		assertEquals(ID_EVENT1, r.getIdEvent().longValue());
		assertNotNull(r.getDtStart());
		assertEquals(r.getDtStart().plusHours(1), r.getDtEnd());
		assertEquals(1, r.getInterval().shortValue());
		assertNull(r.getUntil());
	}

	@Test
	public void testAsWeeklyRecurrence() {
		RecurrentBooking r = RecurrentBooking.asWeeklyRecurrence(ID_RESOURCE1, ID_EVENT1, DTSTART1, DTEND1, INTERVAL1,
				UNTIL1, BY_DAY1);
		assertEquals(Frequency.WEEKLY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource().longValue());
		assertEquals(ID_EVENT1, r.getIdEvent().longValue());
		assertNotNull(r.getDtStart());
		assertEquals(DTEND1, r.getDtEnd());
		assertEquals(INTERVAL1, r.getInterval().shortValue());
		assertEquals(UNTIL1, r.getUntil());
		assertEquals(BY_DAY1, r.getByDay());
	}

	@Test
	public void testAsWeeklyRecurrenceWithDefaultArgs() {
		RecurrentBooking r = RecurrentBooking.asWeeklyRecurrence(ID_RESOURCE1, ID_EVENT1, null, null, null, null, null);
		assertEquals(Frequency.WEEKLY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource().longValue());
		assertEquals(ID_EVENT1, r.getIdEvent().longValue());
		assertNotNull(r.getDtStart());
		assertEquals(r.getDtStart().plusHours(1), r.getDtEnd());
		assertEquals(1, r.getInterval().shortValue());
		assertNull(r.getUntil());
		assertNotNull(r.getByDay());
		assertTrue(r.getByDay().isEmpty());
	}

	@Test
	public void testAsMonthlyRecurrence() {
		RecurrentBooking r = RecurrentBooking.asMonthlyRecurrence(ID_RESOURCE1, ID_EVENT1, DTSTART1, DTEND1, INTERVAL1,
				UNTIL1, BY_DAYOFMONTH1);
		assertEquals(Frequency.MONTHLY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource().longValue());
		assertEquals(ID_EVENT1, r.getIdEvent().longValue());
		assertNotNull(r.getDtStart());
		assertEquals(DTEND1, r.getDtEnd());
		assertEquals(INTERVAL1, r.getInterval().shortValue());
		assertEquals(UNTIL1, r.getUntil());
		assertEquals(BY_DAYOFMONTH1, r.getByDayOfMonth());
	}

	@Test
	public void testAsMonthlyRecurrenceWithDefaultArgs() {
		RecurrentBooking r = RecurrentBooking
				.asMonthlyRecurrence(ID_RESOURCE1, ID_EVENT1, null, null, null, null, null);
		assertEquals(Frequency.MONTHLY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource().longValue());
		assertEquals(ID_EVENT1, r.getIdEvent().longValue());
		assertNotNull(r.getDtStart());
		assertEquals(r.getDtStart().plusHours(1), r.getDtEnd());
		assertEquals(1, r.getInterval().shortValue());
		assertNull(r.getUntil());
		assertNotNull(r.getByDayOfMonth());
		assertTrue(r.getByDayOfMonth().isEmpty());
	}

	@Test
	public void testAsYearlyRecurrence() {
		RecurrentBooking r = RecurrentBooking.asYearlyRecurrence(ID_RESOURCE1, ID_EVENT1, DTSTART1, DTEND1, INTERVAL1,
				UNTIL1, BY_DAYOFMONTH1, BY_MONTH1);
		assertEquals(Frequency.YEARLY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource().longValue());
		assertEquals(ID_EVENT1, r.getIdEvent().longValue());
		assertNotNull(r.getDtStart());
		assertEquals(DTEND1, r.getDtEnd());
		assertEquals(INTERVAL1, r.getInterval().shortValue());
		assertEquals(UNTIL1, r.getUntil());
		assertEquals(BY_DAYOFMONTH1, r.getByDayOfMonth());
	}

	@Test
	public void testAsYearlyRecurrenceWithDefaultArgs() {
		RecurrentBooking r = RecurrentBooking.asYearlyRecurrence(ID_RESOURCE1, ID_EVENT1, null, null, null, null, null,
				null);
		assertEquals(Frequency.YEARLY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource().longValue());
		assertEquals(ID_EVENT1, r.getIdEvent().longValue());
		assertNotNull(r.getDtStart());
		assertEquals(r.getDtStart().plusHours(1), r.getDtEnd());
		assertEquals(1, r.getInterval().shortValue());
		assertNull(r.getUntil());
		assertNotNull(r.getByDayOfMonth());
		assertTrue(r.getByDayOfMonth().isEmpty());
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
