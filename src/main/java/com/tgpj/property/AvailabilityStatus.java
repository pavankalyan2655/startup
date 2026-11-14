package com.tgpj.property;

public enum AvailabilityStatus {
	AVAILABLE,
	LIMITED,
	SOLD_OUT,
	NEW_LAUNCH,
	READY_TO_MOVE,
	UNDER_CONSTRUCTION;

	public static AvailabilityStatus fromApiValue(String value) {
		if (value == null || value.isBlank()) {
			return AVAILABLE;
		}
		String normalized = value.trim().toUpperCase(java.util.Locale.ROOT);
		if ("LIMITED_INVENTORY".equals(normalized)) {
			return LIMITED;
		}
		return AvailabilityStatus.valueOf(normalized);
	}
}
