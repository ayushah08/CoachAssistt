package Coach_Service.dto.notices;

import java.time.LocalDateTime;

public record NoticeView(Long id, String title, String content, NoticeVisibility visibility,
        NoticeRecipientType recipientType, Long studentId, String studentName, String parentName,
        String coachingName, LocalDateTime createdAt, LocalDateTime expiresAt) {}
