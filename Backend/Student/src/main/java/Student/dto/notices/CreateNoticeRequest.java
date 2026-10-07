package Student.dto.notices;

import Student.entity.NoticeRecipientType;
import Student.entity.NoticeVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class CreateNoticeRequest {
    @NotBlank @Size(max = 140) private String title;
    @NotBlank @Size(max = 5000) private String content;
    @NotNull private NoticeVisibility visibility;
    private NoticeRecipientType recipientType;
    private Long studentId;
    private Long parentId;
    private LocalDateTime expiresAt;
}
