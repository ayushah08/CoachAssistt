package Parent.dto.attendance;

import Parent.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor
public class MarkAttendanceRequest {
    @NotNull
    private LocalDate date;
    @NotNull
    private AttendanceStatus status;
}
