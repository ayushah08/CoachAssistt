package Parent.dto.notices;

import Parent.entity.Notice;
import Parent.entity.NoticeRecipientType;
import Parent.entity.NoticeVisibility;
import java.time.LocalDateTime;

public record NoticeView(Long id, String title, String content, NoticeVisibility visibility,
        NoticeRecipientType recipientType, Long studentId, String studentName, String parentName,
        String coachingName, LocalDateTime createdAt, LocalDateTime expiresAt) {
    public static NoticeView from(Notice notice) {
        return new NoticeView(notice.getId(), notice.getTitle(), notice.getContent(), notice.getVisibility(),
                notice.getRecipientType(), notice.getTargetStudentId(), notice.getTargetStudentName(), notice.getTargetParentName(),
                notice.getCoachingName(), notice.getCreatedAt(), notice.getExpiresAt());
    }
    public static NoticeView forRecipient(Notice notice) {
        return new NoticeView(notice.getId(), notice.getTitle(), notice.getContent(), notice.getVisibility(),
                notice.getRecipientType(), notice.getTargetStudentId(), null, null,
                notice.getCoachingName(), notice.getCreatedAt(), notice.getExpiresAt());
    }
}
