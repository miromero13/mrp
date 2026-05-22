package eccomerce.backend_eccomerce.production.enums;

public enum MpsPeriodMode {
    WEEKLY,
    MONTHLY;

    public static MpsPeriodMode fromValue(String value) {
        if (value == null || value.isBlank()) {
            return WEEKLY;
        }

        return MpsPeriodMode.valueOf(value.trim().toUpperCase());
    }
}
