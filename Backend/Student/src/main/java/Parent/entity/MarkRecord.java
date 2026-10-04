package Parent.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_marks", indexes = @Index(name = "idx_student_marks_student_date", columnList = "student_id, assessment_date"))
@Getter @Setter @Builder @NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
public class MarkRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long studentId;
    @Column(nullable = false)
    private String coachingName;
    @Column(nullable = false)
    private String assessmentName;
    @Column(nullable = false)
    private String subject;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal obtainedMarks;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalMarks;
    @Column(nullable = false)
    private LocalDate assessmentDate;
    @Column(length = 500)
    private String remarks;
    @Column(nullable = false)
    private String markedBy;
    @Column(nullable = false)
    private LocalDateTime markedAt;
}
