package edu.upc.cpl.smeagol.client.domain;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The simplest possible booking is represented by a continuos time interval.
 * <p>
 * Internally, the class holds the representation of the interval.
 * 
 * @see org.joda.time.Interval
 * 
 * @author angel
 * 
 */
public class SimpleBooking extends Booking {

	private static Logger logger = Logger.getLogger(Booking.class);

	private Interval interval;

	public SimpleBooking() {
	}

	public SimpleBooking(Integer id, Interval interval) {
		this.setId(id);
		this.interval = interval;
	}

	@Override
	public DateTime getStart() {
		return this.interval.getStart();
	}

	@Override
	public DateTime getEnd() {
		return this.interval.getEnd();
	}

	@Override
	public String toJSONString() {
		JSONObject jo = new JSONObject();
		try {
			jo.put("id", getId());
			jo.put("start", this.interval.getStart().toString());
			jo.put("end", this.interval.getEnd().toString());
		} catch (JSONException e) {
			logger.error(e.getLocalizedMessage());
		}
		return jo.toString();
	}

	@Override
	public Booking fromJSONString(String json) throws JSONException, IllegalArgumentException {
		JSONObject jo = new JSONObject(json);
		Integer id = jo.getInt("id");
		DateTime start = new DateTime(jo.getString("start"));
		DateTime end = new DateTime(jo.getString("end"));
		Interval interval = new Interval(start, end);

		return new SimpleBooking(id, interval);
	}

}
