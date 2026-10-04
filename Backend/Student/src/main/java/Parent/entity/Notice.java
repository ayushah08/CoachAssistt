package Parent.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notices", indexes = {
        @Index(name = "idx_notices_coaching_created", columnList = "coaching_name, created_at"),
        @Index(name = "idx_notices_student", columnList = "target_student_id")
})
@Getter @Builder @NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
public class Notice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String coachingName;
    @Column(nullable = false, length = 140)
    private String title;
    @Column(nullable = false, length = 5000)
    private String content;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private NoticeVisibility visibility;
    @Enumerated(EnumType.STRING) @Column(length = 16)
    private NoticeRecipientType recipientType;
    private Long targetStudentId;
    private Long targetParentId;
    private String targetParentName;
    private String targetStudentName;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
