package edu.upc.cpl.smeagol.client.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.upc.cpl.smeagol.client.ical.DayOfWeek;
import edu.upc.cpl.smeagol.client.ical.Frequency;

public class BookingTest extends TestCase {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(BookingTest.class);

	private static final Long ID1 = 1L;
	private static final Long ID2 = 2L;
	private static final Long ID_RESOURCE1 = 111L;
	private static final Long ID_RESOURCE2 = 2L;
	private static final Long ID_EVENT1 = 222L;
	private static final Long ID_EVENT2 = 20L;
	private static final DateTime DTSTART1 = new DateTime(2011, 2, 3, 8, 0, 0, 0);
	private static final DateTime DTSTART2 = new DateTime("2011-03-02T15:00:00");
	private static final DateTime DTEND1 = new DateTime(2011, 2, 3, 10, 30, 0, 0);
	private static final DateTime DTEND2 = new DateTime("2011-03-02T16:00:00");
	private static final Short INTERVAL1 = 2;
	private static final Short INTERVAL2 = 4;
	private static final DateTime UNTIL1 = new DateTime(2011, 7, 9, 0, 0, 0, 0);
	private static final DateTime UNTIL2 = new DateTime("2011-10-01T20:00:00");
	private static final Frequency FREQUENCY1 = Frequency.WEEKLY;
	private static final Frequency FREQUENCY2 = Frequency.WEEKLY;
	private static final Set<DayOfWeek> BY_DAY1 = new TreeSet<DayOfWeek>();
	private static final Set<Short> BY_DAYOFMONTH1 = new TreeSet<Short>();
	private static final Set<Short> BY_MONTH1 = new TreeSet<Short>();

	private static Booking B1;
	private static Booking B2;

	private static JsonObject B1_AS_JSON_OBJECT;
	private static JsonObject B2_AS_JSON_OBJECT;

	@Before
	@Override
	public void setUp() throws Exception {
		B1 = new Booking();
		B1.setId(ID1);
		B1.setIdResource(ID_RESOURCE1);
		B1.setIdEvent(ID_EVENT1);
		B1.setDtStart(DTSTART1);
		B1.setDtEnd(DTEND1);
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
		B1.setInterval(INTERVAL1);
		B1.setUntil(UNTIL1);

		B2 = new Booking();
		B2.setId(ID2);
		B2.setIdResource(ID_RESOURCE2);
		B2.setIdEvent(ID_EVENT2);
		B2.setDtStart(DTSTART2);
		B2.setDtEnd(DTEND2);
		B2.setFrequency(FREQUENCY2);
		B2.setInterval(INTERVAL2);
		B2.setUntil(UNTIL2);

		B1_AS_JSON_OBJECT = new JsonObject();
		B1_AS_JSON_OBJECT.addProperty("id", B1.getId());
		B1_AS_JSON_OBJECT.addProperty("id_resource", B1.getIdResource());
		B1_AS_JSON_OBJECT.addProperty("id_event", B1.getIdEvent());
		B1_AS_JSON_OBJECT.addProperty("dtstart", B1.getDtStart().toString());
		B1_AS_JSON_OBJECT.addProperty("dtend", B1.getDtEnd().toString());
		B1_AS_JSON_OBJECT.addProperty("frequency", B1.getFrequency().toString());
		B1_AS_JSON_OBJECT.addProperty("interval", B1.getInterval());
		B1_AS_JSON_OBJECT.addProperty("until", B1.getUntil().toString());

		B2_AS_JSON_OBJECT = new JsonObject();
		B2_AS_JSON_OBJECT.addProperty("id", B2.getId());
		B2_AS_JSON_OBJECT.addProperty("id_resource", B2.getIdResource());
		B2_AS_JSON_OBJECT.addProperty("id_event", B2.getIdEvent());
		B2_AS_JSON_OBJECT.addProperty("dtstart", B2.getDtStart().toString());
		B2_AS_JSON_OBJECT.addProperty("dtend", B2.getDtEnd().toString());
		B2_AS_JSON_OBJECT.addProperty("frequency", B2.getFrequency().toString());
		B2_AS_JSON_OBJECT.addProperty("interval", B2.getInterval());
		B2_AS_JSON_OBJECT.addProperty("until", B2.getUntil().toString());
	}

	@Test
	public void testAsSimple() {
		Booking b = Booking.asSimple(ID_RESOURCE1, ID_EVENT1, DTSTART1, DTEND1);
		assertEquals(ID_RESOURCE1, b.getIdResource());
		assertEquals(ID_EVENT1, b.getIdEvent());
		assertEquals(DTSTART1, b.getDtStart());
		assertEquals(DTEND1, b.getDtEnd());
	}

	@Test
	public void testAsDailyRecurrence() {
		Booking r = Booking.asDailyRecurrence(ID_RESOURCE1, ID_EVENT1, DTSTART1, DTEND1, INTERVAL1, UNTIL1);
		assertEquals(Frequency.DAILY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource());
		assertEquals(ID_EVENT1, r.getIdEvent());
		assertNotNull(r.getDtStart());
		assertEquals(DTEND1, r.getDtEnd());
		assertEquals(INTERVAL1, r.getInterval());
		assertEquals(UNTIL1, r.getUntil());
	}

	@Test
	public void testAsDailyRecurrenceWithDefaultArgs() {
		Booking r = Booking.asDailyRecurrence(ID_RESOURCE1, ID_EVENT1, null, null, null, null);
		assertEquals(Frequency.DAILY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource());
		assertEquals(ID_EVENT1, r.getIdEvent());
		assertNotNull(r.getDtStart());
		assertEquals(r.getDtStart().plusHours(1), r.getDtEnd());
		assertEquals(1, r.getInterval().shortValue());
		assertNull(r.getUntil());
	}

	@Test
	public void testAsWeeklyRecurrence() {
		Booking r = Booking.asWeeklyRecurrence(ID_RESOURCE1, ID_EVENT1, DTSTART1, DTEND1, INTERVAL1, UNTIL1, BY_DAY1);
		assertEquals(Frequency.WEEKLY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource());
		assertEquals(ID_EVENT1, r.getIdEvent());
		assertNotNull(r.getDtStart());
		assertEquals(DTEND1, r.getDtEnd());
		assertEquals(INTERVAL1, r.getInterval());
		assertEquals(UNTIL1, r.getUntil());
		assertEquals(BY_DAY1, r.getByDay());
	}

	@Test
	public void testAsWeeklyRecurrenceWithDefaultArgs() {
		Booking r = Booking.asWeeklyRecurrence(ID_RESOURCE1, ID_EVENT1, null, null, null, null, null);
		assertEquals(Frequency.WEEKLY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource());
		assertEquals(ID_EVENT1, r.getIdEvent());
		assertNotNull(r.getDtStart());
		assertEquals(r.getDtStart().plusHours(1), r.getDtEnd());
		assertEquals(1, r.getInterval().shortValue());
		assertNull(r.getUntil());
		assertNotNull(r.getByDay());
		assertTrue(r.getByDay().isEmpty());
	}

	@Test
	public void testAsMonthlyRecurrence() {
		Booking r = Booking.asMonthlyRecurrence(ID_RESOURCE1, ID_EVENT1, DTSTART1, DTEND1, INTERVAL1, UNTIL1,
				BY_DAYOFMONTH1);
		assertEquals(Frequency.MONTHLY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource());
		assertEquals(ID_EVENT1, r.getIdEvent());
		assertNotNull(r.getDtStart());
		assertEquals(DTEND1, r.getDtEnd());
		assertEquals(INTERVAL1, r.getInterval());
		assertEquals(UNTIL1, r.getUntil());
		assertEquals(BY_DAYOFMONTH1, r.getByDayOfMonth());
	}

	@Test
	public void testAsMonthlyRecurrenceWithDefaultArgs() {
		Booking r = Booking.asMonthlyRecurrence(ID_RESOURCE1, ID_EVENT1, null, null, null, null, null);
		assertEquals(Frequency.MONTHLY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource());
		assertEquals(ID_EVENT1, r.getIdEvent());
		assertNotNull(r.getDtStart());
		assertEquals(r.getDtStart().plusHours(1), r.getDtEnd());
		assertEquals(1, r.getInterval().shortValue());
		assertNull(r.getUntil());
		assertNotNull(r.getByDayOfMonth());
		assertTrue(r.getByDayOfMonth().isEmpty());
	}

	@Test
	public void testAsYearlyRecurrence() {
		Booking r = Booking.asYearlyRecurrence(ID_RESOURCE1, ID_EVENT1, DTSTART1, DTEND1, INTERVAL1, UNTIL1,
				BY_DAYOFMONTH1, BY_MONTH1);
		assertEquals(Frequency.YEARLY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource());
		assertEquals(ID_EVENT1, r.getIdEvent());
		assertNotNull(r.getDtStart());
		assertEquals(DTEND1, r.getDtEnd());
		assertEquals(INTERVAL1, r.getInterval());
		assertEquals(UNTIL1, r.getUntil());
		assertEquals(BY_DAYOFMONTH1, r.getByDayOfMonth());
	}

	@Test
	public void testAsYearlyRecurrenceWithDefaultArgs() {
		Booking r = Booking.asYearlyRecurrence(ID_RESOURCE1, ID_EVENT1, null, null, null, null, null, null);
		assertEquals(Frequency.YEARLY, r.getFrequency());
		assertEquals(ID_RESOURCE1, r.getIdResource());
		assertEquals(ID_EVENT1, r.getIdEvent());
		assertNotNull(r.getDtStart());
		assertEquals(r.getDtStart().plusHours(1), r.getDtEnd());
		assertEquals(1, r.getInterval().shortValue());
		assertNull(r.getUntil());
		assertNotNull(r.getByDayOfMonth());
		assertTrue(r.getByDayOfMonth().isEmpty());
	}

	@Test
	public void testSerializeSimple() {
		Booking b = Booking.asSimple(ID_RESOURCE1, ID_EVENT1, DTSTART1, DTEND1);
		b.setId(ID1);
		String json = b.serialize();
		JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
		assertTrue(obj.has("id"));
		assertEquals((long) ID1, obj.get("id").getAsLong());
		assertTrue(obj.has("id_resource"));
		assertEquals((long) ID_RESOURCE1, obj.get("id_resource").getAsLong());
		assertTrue(obj.has("id_event"));
		assertEquals((long) ID_EVENT1, obj.get("id_event").getAsLong());
		assertTrue(obj.has("dtstart"));
		assertEquals(DTSTART1, new DateTime(obj.get("dtstart").getAsString()));
		assertTrue(obj.has("dtend"));
		assertEquals(DTEND1, new DateTime(obj.get("dtend").getAsString()));
	}
	
	@Test
	public void testSerializeDailyRecurrence() {
		Booking b = Booking.asDailyRecurrence(ID_RESOURCE1, ID_EVENT1, DTSTART1, DTEND1, INTERVAL1, UNTIL1);
		b.setId(ID1);
		String json = b.serialize();
		JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
		assertTrue(obj.has("id"));
		assertEquals((long) ID1, obj.get("id").getAsLong());
		assertTrue(obj.has("id_resource"));
		assertEquals((long) ID_RESOURCE1, obj.get("id_resource").getAsLong());
		assertTrue(obj.has("id_event"));
		assertEquals((long) ID_EVENT1, obj.get("id_event").getAsLong());
		assertTrue(obj.has("dtstart"));
		assertEquals(DTSTART1, new DateTime(obj.get("dtstart").getAsString()));
		assertTrue(obj.has("dtend"));
		assertEquals(DTEND1, new DateTime(obj.get("dtend").getAsString()));
		assertTrue(obj.has("frequency"));
		assertEquals(Frequency.DAILY, Frequency.valueOf(obj.get("frequency").getAsString()));
		assertTrue(obj.has("interval"));
		assertEquals((short) INTERVAL1, obj.get("interval").getAsShort());
		assertTrue(obj.has("until"));
		assertEquals(UNTIL1, new DateTime(obj.get("until").getAsString()));
	}

	@Test
	public void testSerializeWeeklyRecurrence() {
		Booking b = Booking.asWeeklyRecurrence(ID_RESOURCE1, ID_EVENT1, DTSTART1, DTEND1, INTERVAL1, UNTIL1, BY_DAY1);
		b.setId(ID1);
		String json = b.serialize();
		JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
		assertTrue(obj.has("id"));
		assertEquals((long) ID1, obj.get("id").getAsLong());
		assertTrue(obj.has("id_resource"));
		assertEquals((long) ID_RESOURCE1, obj.get("id_resource").getAsLong());
		assertTrue(obj.has("id_event"));
		assertEquals((long) ID_EVENT1, obj.get("id_event").getAsLong());
		assertTrue(obj.has("dtstart"));
		assertEquals(DTSTART1, new DateTime(obj.get("dtstart").getAsString()));
		assertTrue(obj.has("dtend"));
		assertEquals(DTEND1, new DateTime(obj.get("dtend").getAsString()));
		assertTrue(obj.has("frequency"));
		assertEquals(Frequency.WEEKLY, Frequency.valueOf(obj.get("frequency").getAsString()));
		assertTrue(obj.has("interval"));
		assertEquals((short) INTERVAL1, obj.get("interval").getAsShort());
		assertTrue(obj.has("until"));
		assertEquals(UNTIL1, new DateTime(obj.get("until").getAsString()));
		assertTrue(obj.has("by_day"));
		Set<DayOfWeek> dow = new TreeSet<DayOfWeek>();
		for (String day : obj.get("by_day").getAsString().split(",")) {
			dow.add(new DayOfWeek(day));
		}
		assertTrue(CollectionUtils.isEqualCollection(dow, BY_DAY1));
	}

	@Test
	public void testSerializeMonthlyRecurrence() {
		Booking b = Booking.asMonthlyRecurrence(ID_RESOURCE1, ID_EVENT1, DTSTART1, DTEND1, INTERVAL1, UNTIL1,
				BY_DAYOFMONTH1);
		b.setId(ID1);
		String json = b.serialize();
		JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
		assertTrue(obj.has("id"));
		assertEquals((long) ID1, obj.get("id").getAsLong());
		assertTrue(obj.has("id_resource"));
		assertEquals((long) ID_RESOURCE1, obj.get("id_resource").getAsLong());
		assertTrue(obj.has("id_event"));
		assertEquals((long) ID_EVENT1, obj.get("id_event").getAsLong());
		assertTrue(obj.has("dtstart"));
		assertEquals(DTSTART1, new DateTime(obj.get("dtstart").getAsString()));
		assertTrue(obj.has("dtend"));
		assertEquals(DTEND1, new DateTime(obj.get("dtend").getAsString()));
		assertTrue(obj.has("frequency"));
		assertEquals(Frequency.MONTHLY, Frequency.valueOf(obj.get("frequency").getAsString()));
		assertTrue(obj.has("interval"));
		assertEquals((short) INTERVAL1, obj.get("interval").getAsShort());
		assertTrue(obj.has("until"));
		assertEquals(UNTIL1, new DateTime(obj.get("until").getAsString()));
		assertTrue(obj.has("by_day_month"));
		Set<Short> dom = new TreeSet<Short>();
		for (String day : obj.get("by_day_month").getAsString().split(",")) {
			dom.add(new Short(day));
		}
		assertTrue(CollectionUtils.isEqualCollection(dom, BY_DAYOFMONTH1));
	}

	@Test
	public void testSerializeYearlyRecurrence() {
		Booking b = Booking.asYearlyRecurrence(ID_RESOURCE1, ID_EVENT1, DTSTART1, DTEND1, INTERVAL1, UNTIL1,
				BY_DAYOFMONTH1, BY_MONTH1);
		b.setId(ID1);
		String json = b.serialize();
		JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
		assertTrue(obj.has("id"));
		assertEquals((long) ID1, obj.get("id").getAsLong());
		assertTrue(obj.has("id_resource"));
		assertEquals((long) ID_RESOURCE1, obj.get("id_resource").getAsLong());
		assertTrue(obj.has("id_event"));
		assertEquals((long) ID_EVENT1, obj.get("id_event").getAsLong());
		assertTrue(obj.has("dtstart"));
		assertEquals(DTSTART1, new DateTime(obj.get("dtstart").getAsString()));
		assertTrue(obj.has("dtend"));
		assertEquals(DTEND1, new DateTime(obj.get("dtend").getAsString()));
		assertTrue(obj.has("frequency"));
		assertEquals(Frequency.YEARLY, Frequency.valueOf(obj.get("frequency").getAsString()));
		assertTrue(obj.has("interval"));
		assertEquals((short) INTERVAL1, obj.get("interval").getAsShort());
		assertTrue(obj.has("until"));
		assertEquals(UNTIL1, new DateTime(obj.get("until").getAsString()));
		assertTrue(obj.has("by_day_month"));
		Set<Short> dom = new TreeSet<Short>();
		for (String day : obj.get("by_day_month").getAsString().split(",")) {
			dom.add(new Short(day));
		}
		assertTrue(CollectionUtils.isEqualCollection(dom, BY_DAYOFMONTH1));
		assertTrue(obj.has("by_month"));
		Set<Short> months = new TreeSet<Short>();
		for (String m : obj.get("by_month").getAsString().split(",")) {
			months.add(new Short(m));
		}
		assertTrue(CollectionUtils.isEqualCollection(BY_MONTH1, months));
	}

	@Test
	public void testSerializeCollection() {
		Booking b1 = Booking.asDailyRecurrence(ID_RESOURCE1, ID_EVENT1, DTSTART1, DTEND1, INTERVAL1, UNTIL1);
		Booking b2 = Booking.asDailyRecurrence(ID_RESOURCE2, ID_EVENT2, DTSTART2, DTEND2, INTERVAL2, UNTIL2);

		b1.setId(ID1);
		b2.setId(ID2);

		List<Booking> bookings = new ArrayList<Booking>();
		bookings.add(b1);
		bookings.add(b2);

		String json = Booking.serialize(bookings);
		JsonArray arr = new JsonParser().parse(json).getAsJsonArray();

		assertEquals("serialization contains all bookings", bookings.size(), arr.size());

		JsonObject obj1 = arr.get(0).getAsJsonObject();
		JsonObject obj2 = arr.get(1).getAsJsonObject();

		assertTrue(obj1.has("id"));
		assertTrue(obj2.has("id"));
		assertEquals((long) ID1, obj1.get("id").getAsLong());
		assertEquals((long) ID2, obj2.get("id").getAsLong());

		assertTrue(obj1.has("id_resource"));
		assertTrue(obj2.has("id_resource"));
		assertEquals((long) ID_RESOURCE1, obj1.get("id_resource").getAsLong());
		assertEquals((long) ID_RESOURCE2, obj2.get("id_resource").getAsLong());

		assertTrue(obj1.has("id_event"));
		assertTrue(obj2.has("id_event"));
		assertEquals((long) ID_EVENT1, obj1.get("id_event").getAsLong());
		assertEquals((long) ID_EVENT2, obj2.get("id_event").getAsLong());

		assertTrue(obj1.has("dtstart"));
		assertTrue(obj2.has("dtstart"));
		assertEquals(DTSTART1, new DateTime(obj1.get("dtstart").getAsString()));
		assertEquals(DTSTART2, new DateTime(obj2.get("dtstart").getAsString()));

		assertTrue(obj1.has("dtend"));
		assertTrue(obj2.has("dtend"));
		assertEquals(DTEND1, new DateTime(obj1.get("dtend").getAsString()));
		assertEquals(DTEND2, new DateTime(obj2.get("dtend").getAsString()));

		assertTrue(obj1.has("frequency"));
		assertTrue(obj2.has("frequency"));
		assertEquals(Frequency.DAILY, Frequency.valueOf(obj1.get("frequency").getAsString()));
		assertEquals(Frequency.DAILY, Frequency.valueOf(obj2.get("frequency").getAsString()));

		assertTrue(obj1.has("interval"));
		assertTrue(obj2.has("interval"));
		assertEquals((short) INTERVAL1, obj1.get("interval").getAsShort());
		assertEquals((short) INTERVAL2, obj2.get("interval").getAsShort());

		assertTrue(obj1.has("until"));
		assertTrue(obj2.has("until"));
		assertEquals(UNTIL1, new DateTime(obj1.get("until").getAsString()));
		assertEquals(UNTIL2, new DateTime(obj2.get("until").getAsString()));
	}

	@Test
	public void testDeserializeSimple() {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("id", B1.getId());
		jsonObj.addProperty("id_resource", B1.getIdResource());
		jsonObj.addProperty("id_event", B1.getIdEvent());
		jsonObj.addProperty("dtstart", B1.getDtStart().toString());
		jsonObj.addProperty("dtend", B1.getDtEnd().toString());
		
		Booking b = Booking.deserialize(jsonObj.toString());
		assertEquals(ID1, b.getId());
		assertEquals(ID_RESOURCE1, b.getIdResource());
		assertEquals(ID_EVENT1, b.getIdEvent());
		assertEquals(DTSTART1, b.getDtStart());
		assertEquals(DTEND1, b.getDtEnd());		
	}
	
	@Test
	public void testDeserializeDailyRecurrence() {
		Booking b = Booking.deserialize(B1_AS_JSON_OBJECT.toString());
		assertEquals(ID1, b.getId());
		assertEquals(ID_RESOURCE1, b.getIdResource());
		assertEquals(ID_EVENT1, b.getIdEvent());
		assertEquals(DTSTART1, b.getDtStart());
		assertEquals(DTEND1, b.getDtEnd());
		assertEquals(FREQUENCY1, b.getFrequency());
		assertEquals(INTERVAL1, b.getInterval());
		assertEquals(UNTIL1, b.getUntil());
	}

	@Test
	public void testDeserializeWeeklyRecurrence() {
		String BY_DAY = "MO,WE,FR";
		int DAY_COUNT = 3;
		JsonObject obj = new JsonObject();

		obj.addProperty("id", ID1);
		obj.addProperty("id_resource", ID_RESOURCE1);
		obj.addProperty("id_event", ID_EVENT1);
		obj.addProperty("dtstart", DTSTART1.toString());
		obj.addProperty("dtend", DTEND1.toString());
		obj.addProperty("frequency", Frequency.WEEKLY.toString());
		obj.addProperty("interval", INTERVAL1);
		obj.addProperty("until", UNTIL1.toString());
		obj.addProperty("by_day", BY_DAY);

		String json = obj.toString();
		Booking b = Booking.deserialize(json);
		assertEquals(ID1, b.getId());
		assertEquals(ID_RESOURCE1, b.getIdResource());
		assertEquals(ID_EVENT1, b.getIdEvent());
		assertEquals(DTSTART1, b.getDtStart());
		assertEquals(DTEND1, b.getDtEnd());
		assertEquals(Frequency.WEEKLY, b.getFrequency());
		assertEquals(INTERVAL1, b.getInterval());
		assertEquals(UNTIL1, b.getUntil());
		assertEquals(DAY_COUNT, b.getByDay().size());
		assertTrue(b.getByDay().contains(DayOfWeek.MONDAY));
		assertTrue(b.getByDay().contains(DayOfWeek.WEDNESDAY));
		assertTrue(b.getByDay().contains(DayOfWeek.FRIDAY));
	}

	@Test
	public void testDeserializeMonthlyRecurrence() {
		String BY_MONTH_DAY = "10,15,-1";
		int DAY_COUNT = 3;
		JsonObject obj = new JsonObject();

		obj.addProperty("id", ID1);
		obj.addProperty("id_resource", ID_RESOURCE1);
		obj.addProperty("id_event", ID_EVENT1);
		obj.addProperty("dtstart", DTSTART1.toString());
		obj.addProperty("dtend", DTEND1.toString());
		obj.addProperty("frequency", Frequency.MONTHLY.toString());
		obj.addProperty("interval", INTERVAL1);
		obj.addProperty("until", UNTIL1.toString());
		obj.addProperty("by_day_month", BY_MONTH_DAY);

		String json = obj.toString();
		Booking b = Booking.deserialize(json);
		assertEquals(ID1, b.getId());
		assertEquals(ID_RESOURCE1, b.getIdResource());
		assertEquals(ID_EVENT1, b.getIdEvent());
		assertEquals(DTSTART1, b.getDtStart());
		assertEquals(DTEND1, b.getDtEnd());
		assertEquals(Frequency.MONTHLY, b.getFrequency());
		assertEquals(INTERVAL1, b.getInterval());
		assertEquals(UNTIL1, b.getUntil());
		assertEquals(DAY_COUNT, b.getByDayOfMonth().size());
		assertTrue(b.getByDayOfMonth().contains((short) 10));
		assertTrue(b.getByDayOfMonth().contains((short) 15));
		assertTrue(b.getByDayOfMonth().contains((short) -1));
	}

	@Test
	public void testDeserializeYearlyRecurrence() {
		String BY_DAY_OF_MONTH = "1,15,20,-1";
		int DAY_COUNT = 4;
		String BY_MONTH = "1,6,10";
		int MONTH_COUNT = 3;
		JsonObject obj = new JsonObject();

		obj.addProperty("id", ID1);
		obj.addProperty("id_resource", ID_RESOURCE1);
		obj.addProperty("id_event", ID_EVENT1);
		obj.addProperty("dtstart", DTSTART1.toString());
		obj.addProperty("dtend", DTEND1.toString());
		obj.addProperty("frequency", Frequency.YEARLY.toString());
		obj.addProperty("interval", INTERVAL1);
		obj.addProperty("until", UNTIL1.toString());
		obj.addProperty("by_day_month", BY_DAY_OF_MONTH);
		obj.addProperty("by_month", BY_MONTH);

		String json = obj.toString();
		Booking b = Booking.deserialize(json);
		assertEquals(ID1, b.getId());
		assertEquals(ID_RESOURCE1, b.getIdResource());
		assertEquals(ID_EVENT1, b.getIdEvent());
		assertEquals(DTSTART1, b.getDtStart());
		assertEquals(DTEND1, b.getDtEnd());
		assertEquals(Frequency.YEARLY, b.getFrequency());
		assertEquals(INTERVAL1, b.getInterval());
		assertEquals(UNTIL1, b.getUntil());
		assertEquals(DAY_COUNT, b.getByDayOfMonth().size());
		assertTrue(b.getByDayOfMonth().contains((short) 1));
		assertTrue(b.getByDayOfMonth().contains((short) 15));
		assertTrue(b.getByDayOfMonth().contains((short) 20));
		assertTrue(b.getByDayOfMonth().contains((short) -1));
		assertEquals(MONTH_COUNT, b.getByMonth().size());
		assertTrue(b.getByMonth().contains((short) 1));
		assertTrue(b.getByMonth().contains((short) 6));
		assertTrue(b.getByMonth().contains((short) 10));
	}

	@Test
	public void testDeserializeCollection() {
		JsonArray arr = new JsonArray();
		arr.add(B1_AS_JSON_OBJECT);
		arr.add(B2_AS_JSON_OBJECT);
		String json = arr.toString();
		Collection<Booking> bookings = Booking.deserializeCollection(json);

		assertEquals(arr.size(), bookings.size());

		Booking b1 = (Booking) CollectionUtils.get(bookings, 0);
		Booking b2 = (Booking) CollectionUtils.get(bookings, 1);

		assertEquals(ID1, b1.getId());
		assertEquals(ID2, b2.getId());
		assertEquals(ID_RESOURCE1, b1.getIdResource());
		assertEquals(ID_RESOURCE2, b2.getIdResource());
		assertEquals(ID_EVENT1, b1.getIdEvent());
		assertEquals(ID_EVENT2, b2.getIdEvent());
		assertEquals(DTSTART1, b1.getDtStart());
		assertEquals(DTSTART2, b2.getDtStart());
		assertEquals(DTEND1, b1.getDtEnd());
		assertEquals(DTEND2, b2.getDtEnd());
		assertEquals(FREQUENCY1, b1.getFrequency());
		assertEquals(FREQUENCY2, b2.getFrequency());
		assertEquals(INTERVAL1, b1.getInterval());
		assertEquals(INTERVAL2, b2.getInterval());
		assertEquals(UNTIL1, b1.getUntil());
		assertEquals(UNTIL2, b2.getUntil());
	}

	@Test
	public void testCompareTo() {
		assertTrue(B1.compareTo(B2) < 0);
		assertTrue(B2.compareTo(B1) > 0);
		assertTrue(B1.compareTo(B1) == 0);
	}

	@Test
	public void testEquals() {
		Booking x = new Booking();
		Booking y = new Booking();
		assertTrue(B1.equals(B1));
		assertFalse(B1.equals(B2));
		assertTrue(x.equals(y));
	}

}
