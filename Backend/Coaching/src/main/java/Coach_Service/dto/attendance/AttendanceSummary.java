package Coach_Service.dto.attendance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor
public class AttendanceSummary {
    private Long studentId;
    private long totalDaysMarked;
    private long presentDays;
    private long absentDays;
    private double attendancePercentage;
    private List<AttendanceEntry> records;
}
