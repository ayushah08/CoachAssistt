package Parent.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AttendanceSummary {
    private Long studentId;
    private long totalDaysMarked;
    private long presentDays;
    private long absentDays;
    private double attendancePercentage;
    private List<AttendanceEntry> records;
}
