package edu.upc.cpl.smeagol.client.domain;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.validator.GenericValidator;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import edu.upc.cpl.smeagol.client.ical.DayOfWeek;
import edu.upc.cpl.smeagol.client.ical.Frequency;
import edu.upc.cpl.smeagol.json.DateTimeConverter;
import edu.upc.cpl.smeagol.json.DayOfWeekListConverter;
import edu.upc.cpl.smeagol.json.ShortSetConverter;

/**
 * An Sméagol booking.
 * <p>
 * In the Sméagol terminology, bookings can be "simple" or "recurrent". Simple
 * bookings have a "start" DateTime and an "end" DateTime. Recurrent bookings
 * may occur several times during a period of time, as defined by RFC 5545.
 * <p>
 * Keep in mind that Sméagol Server API v2.0 implements <strong>only a
 * subset</strong> of this standard. Please refer to <a
 * href="http://tools.ietf.org/html/rfc5545">RFC 5545</a>, section 3.3.10, to
 * get a detailed description of valid recurrence attribute values.
 * <p>
 * TODO: Add details about which features of the RFC subset are implemented.
 * 
 * @author angel
 * 
 */
public class Booking implements Comparable<Booking> {

	private static transient Gson gson = new Gson();

	private static transient Duration MAX_SPAN_DURATION_FOR_DAILY_RECURRENCE = Duration.standardHours(24);
	private static transient Duration MAX_SPAN_DURATION_FOR_WEEKLY_RECURRENCE = Duration.standardDays(7);

	/*
	 * provide custom serializers/deserializers for several attributes
	 */
	static {
		GsonBuilder gb = new GsonBuilder();

		Type setOfDayOfWeekType = new TypeToken<Set<DayOfWeek>>() {
		}.getType();
		Type setOfShortType = new TypeToken<Set<Short>>() {
		}.getType();
		gb.registerTypeAdapter(setOfDayOfWeekType, new DayOfWeekListConverter());
		gb.registerTypeAdapter(setOfShortType, new ShortSetConverter());
		gb.registerTypeAdapter(DateTime.class, new DateTimeConverter());
		gson = gb.create();
	}

	/**
	 * The booking id
	 */
	private Long id;

	/**
	 * The id of the resource related to this booking
	 */
	private Long id_resource;

	/**
	 * The id of the event related to this booking
	 */
	private Long id_event;

	/**
	 * Default dtstart is "now"
	 */
	private DateTime dtstart = new DateTime();

	/**
	 * Default dtend is "dtstart + 1 hour"
	 */
	private DateTime dtend = dtstart.plusHours(1);
	private Frequency frequency;
	private Short interval = 1; // default repeat: "every 1"
	private DateTime until;
	private Set<DayOfWeek> by_day;
	private Set<Short> by_day_month; // valid values: [-1 .. -31, 1 .. 31]
	private Set<Short> by_month; // valid values: [1 .. 12]

	/**
	 * Default non-argument constructor. Needed by google-gson for
	 * deserialization. Protected because users should not be allowed to call it
	 * directly.
	 */
	protected Booking() {
	}

	public static Booking asSimple(Long idResource, Long idEvent, DateTime dtStart, DateTime dtEnd) {
		Booking b = new Booking();
		b.setIdResource(idResource);
		b.setIdEvent(idEvent);
		b.setDtStart(dtStart);
		b.setDtEnd(dtEnd);
		return b;
	}

	/**
	 * Constructor method for bookings which repeat on a daily basis.
	 * <p>
	 * For example, to define a booking for a resource identified by
	 * <code>resId</code> for an event identified by <code>evId</code>, every
	 * day from <code>8:00am</code> to <code>10:00am</code>, starting on
	 * <code>2010-02-01</code> during the next <code>3</code> monts:
	 * 
	 * <pre>
	 * DateTime start = new DateTime(2010, 2, 1, 8, 0, 0);
	 * DateTime end = start.plusHours(2);
	 * DateTime until = start.plusMonths(3);
	 * 
	 * RecurrentBooking booking = RecurrentBooking.asDailyRecurrence(resId, evId, start, end, 1, until);
	 * </pre>
	 * 
	 * @param idResource
	 *            identifier of the <code>Resource</code> to be booked. Not
	 *            null. The resource must exist.
	 * 
	 * @param idEvent
	 *            identifier of the <code>Event</code> related to the booking.
	 *            Not null. The event must exist.
	 * 
	 * @param start
	 *            <code>DateTime</code> at which the booking starts every day.
	 *            <code>null</code> means now.
	 * 
	 * @param end
	 *            the <code>DateTime</code> at which the booking ends every day.
	 *            <code>null</code> means "1 hour after <code>start</code>".
	 * 
	 * @param interval
	 *            the booking will repeat in steps of <code>interval</code>
	 *            days. Must be > 0. Default is 1 (every day).
	 * 
	 * @param until
	 *            the <code>DateTime</code> at which the recurrence ends. If
	 *            null, the booking never ends. If not null, <code>until</code>
	 *            must be greater than <code>start</code> (otherwise, an
	 *            <code>IllegalArgumentException</code> will be thrown).
	 * 
	 * @return a new RecurrentBooking for the specified daily recurrence.
	 * 
	 * @throws IllegalArgumentException
	 */
	public static Booking asDailyRecurrence(Long idResource, Long idEvent, DateTime start, DateTime end,
			Short interval, DateTime until) throws IllegalArgumentException {

		// TODO: Check that end - start <= 24 hours (???)
		
		Interval i = new Interval(start, end);
		if (i.toDuration().isLongerThan(MAX_SPAN_DURATION_FOR_DAILY_RECURRENCE)) {
			throw new IllegalArgumentException(
					"time span defined by [start,end] is too wide (should last at most one day for daily recurrences)");
		}

		if (until != null && until.isBefore(start)) {
			throw new IllegalArgumentException("until DateTime should be after start DateTime");
		}

		Booking b = new Booking();
		b.setIdResource(idResource);
		b.setIdEvent(idEvent);
		b.setDtStart(start);
		b.setDtEnd(end);
		b.setFrequency(Frequency.DAILY);
		b.setInterval(interval);
		b.setUntil(until);

		return b;
	}

	/**
	 * Constructor method for bookings which repeat on a weekly basis.
	 * <p>
	 * For example, to define a booking for a resource identified by
	 * <code>resId</code> for an event identified by <code>evId</code>, all
	 * Mondays and Thursdays, from <code>8:00am</code> to <code>14:00pm</code>,
	 * starting on <code>2010-02-01</code> during the next <code>3</code> monts:
	 * 
	 * <pre>
	 * DateTime start = new DateTime(2010, 2, 1, 8, 0, 0, 0);
	 * DateTime end = start.plusHours(6);
	 * DateTime until = start.plusMonths(3);
	 * 
	 * Set&lt;short&gt; byDay = new HashSet&lt;short&gt;;
	 * byDay.add(DayOfWeek.MONDAY);
	 * byDay.add(DayOfWeek.THURSDAY);
	 * 
	 * RecurrentBooking booking = RecurrentBooking.asWeeklyRecurrence(resId, evId, start, end, 1, until, byDay);
	 * </pre>
	 * 
	 * @param idResource
	 *            identifier of the <code>Resource</code> to be booked. Not
	 *            null. The resource must exist.
	 * 
	 * @param idEvent
	 *            identifier of the <code>Event</code> related to the booking.
	 *            Not null. The event must exist.
	 * 
	 * @param start
	 *            the <code>DateTime</code> at which the booking starts every
	 *            duration day. Default is "now".
	 * 
	 * @param end
	 *            the <code>DateTime</code> at which the booking ends every day.
	 *            Default is 1 hour after <code>start</code>.
	 * 
	 * @param interval
	 *            the booking will repeat in steps of <code>interval</code>
	 *            days. Must be > 0. Default is 1 (every day).
	 * 
	 * @param until
	 *            the <code>DateTime</code> at which the recurrence ends. If
	 *            null, the booking never ends. If not null, <code>until</code>
	 *            must be greater than <code>start</code> (otherwise, an
	 *            <code>IllegalArgumentException</code> will be thrown).
	 * 
	 * @param byDay
	 *            the days of the week at which the booking happens. The
	 *            {@link DayOfWeek} class provides several constants you can use
	 *            to populate this parameter.
	 * 
	 * @throws IllegalArgumentException
	 */
	public static Booking asWeeklyRecurrence(Long idResource, Long idEvent, DateTime start, DateTime end,
			Short interval, DateTime until, Set<DayOfWeek> byDay) throws IllegalArgumentException {

		// TODO: Check that end - start <= 24 hours (???)

		Interval i = new Interval(start, end);
		if (i.toDuration().isLongerThan(MAX_SPAN_DURATION_FOR_WEEKLY_RECURRENCE)) {
			throw new IllegalArgumentException(
					"time span defined by [start,end] is too long (should last at most one week for weekly recurrences)");
		}

		if (until != null && until.isBefore(start)) {
			throw new IllegalArgumentException("until DateTime should be after start DateTime");
		}

		Booking b = new Booking();
		b.setIdResource(idResource);
		b.setIdEvent(idEvent);
		b.setDtStart(start);
		b.setDtEnd(end);
		b.setFrequency(Frequency.WEEKLY);
		b.setByDay(byDay);
		b.setInterval(interval);
		b.setUntil(until);

		return b;
	}

	/**
	 * Constructor method for bookings which repeat on a monthly basis.
	 * <p>
	 * For example, to define a booking for a resource identified by
	 * <code>resId</code> for an event identified by <code>evId</code>, all
	 * mondays and thursdays from <code>8:00am</code> to <code>14:00pm</code>,
	 * starting on <code>2010-02-01</code> during the next <code>3</code> monts:
	 * 
	 * <pre>
	 * DateTime start = new DateTime(2010, 2, 1, 8, 0, 0, 0);
	 * DateTime end = start.plusHours(6);
	 * DateTime until = start.plusMonths(3);
	 * 
	 * Set&lt;short&gt; byDay = new HashSet&lt;short&gt;;
	 * byDay.add(DayOfWeek.MONDAY);
	 * byDay.add(DayOfWeek.THURSDAY);
	 * 
	 * RecurrentBooking booking = RecurrentBooking.asMonthlyRecurrence(resId, evId, start, end, 1, until, byDay);
	 * </pre>
	 * 
	 * @param idResource
	 *            identifier of the <code>Resource</code> to be booked. Not
	 *            null. The resource must exist.
	 * 
	 * @param idEvent
	 *            identifier of the <code>Event</code> related to the booking.
	 *            Not null. The event must exist.
	 * 
	 * @param start
	 *            the <code>DateTime</code> at which the booking starts every
	 *            duration day. Default is "now".
	 * 
	 * @param end
	 *            the <code>DateTime</code> at which the booking ends every day.
	 *            Default is 1 hour after <code>start</code>.
	 * 
	 * @param byDay
	 *            the days of the week at which the booking happens. The
	 *            {@link DayOfWeek} class provides several constants you can use
	 *            to populate this parameter.
	 * 
	 * @param interval
	 *            the booking will repeat in steps of <code>interval</code>
	 *            days. Must be > 0. Default is 1 (every day).
	 * 
	 * @param until
	 *            the <code>DateTime</code> at which the recurrence ends. If
	 *            null, the booking never ends. If not null, <code>until</code>
	 *            must be greater than <code>start</code> (otherwise, an
	 *            <code>IllegalArgumentException</code> will be thrown).
	 * 
	 * @return a new RecurrentBooking for the specified daily recurrence.
	 */
	public static Booking asMonthlyRecurrence(Long idResource, Long idEvent, DateTime start, DateTime end,
			Short interval, DateTime until, Set<Short> byMonthDay) throws IllegalArgumentException {

		// TODO: Check that end - start <= 1 month (???)

		if (until != null && until.isBefore(start)) {
			throw new IllegalArgumentException("until DateTime should be after start DateTime");
		}

		Booking b = new Booking();

		b.setIdResource(idResource);
		b.setIdEvent(idEvent);
		b.setDtStart(start);
		b.setDtEnd(end);
		b.setFrequency(Frequency.MONTHLY);
		b.setByDayOfMonth(byMonthDay);
		b.setInterval(interval);
		b.setUntil(until);

		return b;
	}

	/**
	 * Constructor method for bookings which repeat on a yearly basis.
	 * <p>
	 * For example, to define a booking for a resource identified by
	 * <code>resId</code> for an event identified by <code>evId</code>, every
	 * December the 25th (all day):
	 * 
	 * <pre>
	 * DateTime start = new DateTime(2010, 12, 25, 0, 0, 0, 0);
	 * DateTime end = start.plusDays(1);
	 * 
	 * RecurrentBooking booking = RecurrentBooking.asYearlyRecurrence(resId, evId, start, end, 1, until, byDay);
	 * </pre>
	 * 
	 * @param idResource
	 *            identifier of the <code>Resource</code> to be booked. Not
	 *            null. The resource must exist.
	 * 
	 * @param idEvent
	 *            identifier of the <code>Event</code> related to the booking.
	 *            Not null. The event must exist.
	 * 
	 * @param start
	 *            the <code>DateTime</code> at which the booking starts every
	 *            duration day. Default is "now".
	 * 
	 * @param end
	 *            the <code>DateTime</code> at which the booking ends every day.
	 *            Default is 1 hour after <code>start</code>.
	 * 
	 * @param interval
	 *            the booking will repeat in steps of <code>interval</code>
	 *            days. Must be > 0. Default is 1 (every day).
	 * 
	 * @param until
	 *            the <code>DateTime</code> at which the recurrence ends. If
	 *            null, the booking never ends. If not null, <code>until</code>
	 *            must be greater than <code>start</code> (otherwise, an
	 *            <code>IllegalArgumentException</code> will be thrown).
	 * 
	 * @param byMonthDay
	 *            the days of the month at which the booking happens. See the
	 *            {@link #setByDayOfMonth(Set)} method for valid days of the
	 *            month values.
	 * 
	 * @param byMonth
	 *            the months of the year which are affected by the recurrence.
	 *            Valid months are numbers in the range [1..12].
	 * 
	 * @return a new RecurrentBooking for the specified daily recurrence.
	 */
	public static Booking asYearlyRecurrence(Long idResource, Long idEvent, DateTime start, DateTime end,
			Short interval, DateTime until, Set<Short> byMonthDay, Set<Short> byMonth) {

		// TODO: Check that end - start <= 1 year (???)

		if (until != null && until.isBefore(start)) {
			throw new IllegalArgumentException("until DateTime should be after start DateTime");
		}

		Booking b = new Booking();
		b.setIdResource(idResource);
		b.setIdEvent(idEvent);
		b.setDtStart(start);
		b.setDtEnd(end);
		b.setFrequency(Frequency.YEARLY);
		b.setInterval(interval);
		b.setUntil(until);
		b.setByDayOfMonth(byMonthDay);
		b.setByMonth(byMonth);

		return b;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdResource() {
		return id_resource;
	}

	public void setIdResource(Long id) {
		this.id_resource = id;
	}

	public Long getIdEvent() {
		return id_event;
	}

	public void setIdEvent(Long id) {
		this.id_event = id;
	}

	public Set<DayOfWeek> getByDay() {
		return by_day;
	}

	/**
	 * Set the days of the week which are affected by the recurrence.
	 * 
	 * @param byDay
	 *            a list of {@link DayOfWeek} values
	 */
	public void setByDay(Set<DayOfWeek> byDay) {
		this.by_day = byDay == null ? new TreeSet<DayOfWeek>() : byDay;
	}

	public Set<Short> getByDayOfMonth() {
		return by_day_month;
	}

	/**
	 * Set the days of the month which are affected by the recurrence.
	 * <p>
	 * Negative values start counting backwards from the last day of the month
	 * (i.e., "1" means "first day of the month", whereas "-1" means
	 * "last day of the month").
	 * 
	 * @param byDayOfMonth
	 *            set of values in the ranges [-31 .. -1] or [1 .. 31].
	 * @throws IllegalArgumentException
	 *             if byDayOfMonth contains any illegal value
	 */
	public void setByDayOfMonth(Set<Short> byDayOfMonth) throws IllegalArgumentException {
		if (byDayOfMonth == null) {
			this.by_day_month = new TreeSet<Short>();
		} else {
			for (Short d : byDayOfMonth) {
				if (d == null || (!GenericValidator.isInRange(d, -31, -1) && !GenericValidator.isInRange(d, 1, 31))) {
					throw new IllegalArgumentException(
							"illegal day of month value: only values between ranges [-31 .. -1] or [1 .. 31] are valid");
				}
			}
			this.by_day_month = byDayOfMonth;
		}
	}

	public Set<Short> getByMonth() {
		return by_month;
	}

	/**
	 * Set the months affected by the recurrence.
	 * 
	 * @param byMonth
	 *            set of integer values in the range [1 .. 12]
	 */
	public void setByMonth(Set<Short> byMonth) throws IllegalArgumentException {
		if (byMonth == null) {
			this.by_month = new TreeSet<Short>();
		} else {
			for (Short m : byMonth) {
				if (m == null || (!GenericValidator.isInRange(m, 1, 12))) {
					throw new IllegalArgumentException("illegal month: only values between [1 .. 12] are valid");
				}
			}
			this.by_month = byMonth;
		}
	}

	public DateTime getDtStart() {
		return dtstart;
	}

	public void setDtStart(DateTime dtStart) {
		this.dtstart = (dtStart == null) ? new DateTime() : dtStart;
	}

	public DateTime getDtEnd() {
		return dtend;
	}

	public void setDtEnd(DateTime dtEnd) {
		this.dtend = (dtEnd == null) ? getDtStart().plusHours(1) : dtEnd;
	}

	public Frequency getFrequency() {
		return frequency;
	}

	/**
	 * The </code>Frequency</code> rule part identifies the type of recurrence
	 * rule. Valid values include DAILY, to specify repeating events based on an
	 * interval of a day or more; WEEKLY, to specify repeating events based on
	 * an interval of a week or more; MONTHLY, to specify repeating events based
	 * on an interval of a month or more; and YEARLY, to specify repeating
	 * events based on an interval of a year or more.
	 * 
	 * Sets the frequency of the booking. Its used in combination with
	 * <code>byDay</code>, <code>byDayOfMonth</code>, <code>byHour</code> or
	 * <code>byMinute</code> attributes.
	 * <p>
	 * For example, within a <code>frequency = Frequency.MONTHLY</code> booking,
	 * a <code>byDay = DayOfWeek.MONDAY</code> value represents <em>all Mondays
	 * within the month</em>.
	 * 
	 * @param see
	 *            {@see Frequency}
	 */
	public void setFrequency(Frequency frequency) {
		this.frequency = frequency;
	}

	public Short getInterval() {
		return interval;
	}

	/**
	 * The INTERVAL rule part contains a positive integer representing at which
	 * intervals the recurrence rule repeats. The default value is "1", every
	 * day for a DAILY rule, every week for a WEEKLY rule, every month for a
	 * MONTHLY rule, and every year for a YEARLY rule. For example, within a
	 * DAILY rule, a value of "8" means every eight days.
	 * 
	 * @param interval
	 *            the new interval value. Default is <code>1</code>.
	 */
	public void setInterval(Short interval) {
		if (interval == null || interval.compareTo((short) 1) < 0) {
			this.interval = 1;
		} else {
			this.interval = interval;
		}
	}

	public DateTime getUntil() {
		return until;
	}

	/**
	 * The <code>UNTIL</code> rule part defines a DateTime value that bounds the
	 * recurrence rule in an inclusive manner. If the value specified by UNTIL
	 * is synchronized with the specified recurrence, this DateTime becomes the
	 * last instance of the recurrence.
	 * <p>
	 * If UNTIL is null, the recurrence is considered to repeat forever.
	 * 
	 * @param until
	 *            the <code>DateTime</code> where the recurrence ends.
	 */
	public void setUntil(DateTime until) throws IllegalArgumentException {
		if (until != null && until.isBefore(getDtStart())) {
			throw new IllegalArgumentException("until cannot be before dtStart");
		}
		this.until = until;
	}

	public String serialize() {
		return gson.toJson(this);
	}

	public static String serialize(Collection<Booking> c) {
		return gson.toJson(c);
	}

	public static Booking deserialize(String json) {
		return gson.fromJson(json, Booking.class);
	}

	public static Collection<Booking> deserializeCollection(String json) {
		Type collectionType = new TypeToken<Collection<Booking>>() {
		}.getType();
		return gson.fromJson(json, collectionType);
	}

	/**
	 * Defines natural order between bookings.
	 */
	public int compareTo(Booking b) {
		return new CompareToBuilder().appendSuper(this.getDtStart().compareTo(b.getDtStart()))
				.appendSuper(this.getDtEnd().compareTo(b.getDtEnd())).toComparison();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).append("id_resource", getIdResource())
				.append("id_event", getIdEvent()).append(getDtStart()).append(getDtEnd()).toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (this == o) {
			return true;
		}
		if (!(o instanceof Booking)) {
			return false;
		}
		Booking other = (Booking) o;
		return new EqualsBuilder().append(this.getId(), other.getId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getId()).toHashCode();
	}
}
