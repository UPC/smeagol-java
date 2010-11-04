package edu.upc.cpl.smeagol.client.domain;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

public class SimpleBooking implements Booking {

	private int id;
	private DateTime dtStart;
	private DateTime dtEnd;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDtStart(DateTime dtStart) {
		this.dtStart = dtStart;
	}

	public DateTime getDtStart() {
		return dtStart;
	}

	public void setDtEnd(DateTime dtEnd) {
		this.dtEnd = dtEnd;
	}

	public DateTime getDtEnd() {
		return dtEnd;
	}

	public int compareTo(Booking o) {
		if (this == o) {
			return 0;
		}
		if (!(o instanceof SimpleBooking)) {
			return -1;
		}
		SimpleBooking other = (SimpleBooking) o;
		return new CompareToBuilder().append(this.dtStart, other.dtStart)
				.append(this.dtEnd, other.dtEnd).toComparison();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id)
				.append("dtStart", dtStart).append("dtEnd", dtEnd).toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}
		SimpleBooking other = (SimpleBooking) obj;
		return new EqualsBuilder().append(this.id, other.id).isEquals();
	}

	public String toJSONString() {
		// TODO Auto-generated method stub
		return null;
	}

}
