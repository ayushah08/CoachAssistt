package Coach_Service.dto.marks;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MarkEntry(Long id, Long studentId, String assessmentName, String subject,
        BigDecimal obtainedMarks, BigDecimal totalMarks, double percentage,
        LocalDate assessmentDate, String remarks, String markedBy, LocalDateTime markedAt) {}
