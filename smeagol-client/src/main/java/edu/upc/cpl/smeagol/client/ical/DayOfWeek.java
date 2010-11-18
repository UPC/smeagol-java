package edu.upc.cpl.smeagol.client.ical;

/**
 * Valid days of week, for recurrences.
 * 
 * @author angel
 * 
 */
public enum DayOfWeek {

	MONDAY("MO"), TUESDAY("TU"), WEDNESDAY("WE"), THURSDAY("TH"), FRIDAY("FR"), SATURDAY("SA"), SUNDAY("SU");

	private final String label;

	DayOfWeek(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}

	public static DayOfWeek newFromLabel(String label) throws IllegalArgumentException {
		for (DayOfWeek d : DayOfWeek.values()) {
			if (label.toUpperCase().equals(d.getLabel())) {
				return d;
			}
		}
		throw new IllegalArgumentException("Invalid label for a DayOfWeek");
	}
}
