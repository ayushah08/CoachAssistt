package Coach_Service.dto.attendance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AttendanceStatus {
    PRESENT,
    ABSENT;

    @JsonCreator
    public static AttendanceStatus fromJson(String value) {
        if (value == null) return null;
        return AttendanceStatus.valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
    }

    @JsonValue
    public String toJson() {
        return this == PRESENT ? "Present" : "Absent";
    }
}
