package Coach_Service.dto.attendance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class AttendanceEntry {
    private Long studentId;
    private LocalDate date;
    private AttendanceStatus status;
    private String markedBy;
    private LocalDateTime markedAt;
}
