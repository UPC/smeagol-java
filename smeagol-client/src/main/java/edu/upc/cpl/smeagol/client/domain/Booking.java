package edu.upc.cpl.smeagol.client.domain;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

/**
 * This is the base class of the Booking family. This class defines the minimum
 * behaviour to be implemented by a booking. It barely states that bookings have
 * an id, a reference to the related resource and event, and a natural order
 * defined by some <code>start</code> and <code>end</code> instants.
 * 
 * @author angel
 * 
 */
public abstract class Booking implements Comparable<Booking> {

	private Long id;
	private Long id_resource;
	private Long id_event;

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

	public abstract DateTime getDtStart();

	public abstract DateTime getDtEnd();

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
				.append("id_event", getIdEvent()).toString();
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
