package Student.dto.marks;

import Student.entity.MarkRecord;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MarkEntry(Long id, Long studentId, String assessmentName, String subject,
        BigDecimal obtainedMarks, BigDecimal totalMarks, double percentage,
        LocalDate assessmentDate, String remarks, String markedBy, LocalDateTime markedAt) {
    public static MarkEntry from(MarkRecord m) {
        double percent = m.getTotalMarks().signum() == 0 ? 0 : m.getObtainedMarks()
                .multiply(BigDecimal.valueOf(100)).divide(m.getTotalMarks(), 2, java.math.RoundingMode.HALF_UP).doubleValue();
        return new MarkEntry(m.getId(), m.getStudentId(), m.getAssessmentName(), m.getSubject(),
                m.getObtainedMarks(), m.getTotalMarks(), percent, m.getAssessmentDate(), m.getRemarks(),
                m.getMarkedBy(), m.getMarkedAt());
    }
}
