package edu.upc.cpl.smeagol.client.domain;

import org.json.JSONString;

/**
 * There are two types of Bookings in a Sméagol server: simple bookings and
 * recurrent bookings. Both types must implement this interface.
 * 
 * @author angel
 * 
 */
public interface Booking extends Comparable<Booking>, JSONString {

	public int getId();

	public void setId(int id);

}
