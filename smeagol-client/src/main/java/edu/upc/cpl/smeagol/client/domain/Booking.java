package edu.upc.cpl.smeagol.client.domain;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

/**
 * This is the base class of the Booking family. This class defines the minimum
 * behaviour to be implemented by a booking. It barely states that bookings have
 * an id, a natural order defined by some <code>start</code> and
 * <code>end</code> instants.
 * 
 * @author angel
 * 
 */
public abstract class Booking implements Comparable<Booking> {

	private Integer id;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public abstract DateTime getStart();

	public abstract DateTime getEnd();

	/**
	 * Defines natural order between bookings.
	 */
	public int compareTo(Booking b) {
		return new CompareToBuilder().appendSuper(this.getStart().compareTo(b.getStart()))
				.appendSuper(this.getEnd().compareTo(b.getEnd())).toComparison();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (this == o) {
			return true;
		}
		if (!(o instanceof SimpleBooking)) {
			return false;
		}
		SimpleBooking other = (SimpleBooking) o;
		return new EqualsBuilder().append(this.id, other.getId()).append(this.getStart(), other.getStart())
				.append(this.getEnd(), other.getEnd()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getId()).toHashCode();
	}

}
