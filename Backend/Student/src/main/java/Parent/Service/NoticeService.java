package Parent.Service;

import Parent.dto.notices.CreateNoticeRequest;
import Parent.dto.notices.NoticeView;
import Parent.entity.Notice;
import Parent.entity.NoticeRecipientType;
import Parent.entity.NoticeVisibility;
import Parent.entity.ParentAccount;
import Parent.entity.Student;
import Parent.repository.NoticeRepository;
import Parent.repository.ParentAccountRepository;
import Parent.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository notices;
    private final StudentRepository students;
    private final ParentAccountRepository parents;

    @Transactional
    public NoticeView create(String coachingName, CreateNoticeRequest request) {
        if (request.getVisibility() == NoticeVisibility.PUBLIC) {
            if (request.getStudentId() != null || request.getParentId() != null || request.getRecipientType() != null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Public notices cannot have a student recipient");
        } else if (request.getStudentId() == null || request.getRecipientType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Private notices require a student and recipient type");
        }
        if (request.getVisibility() == NoticeVisibility.PRIVATE
                && (request.getRecipientType() == NoticeRecipientType.PARENT || request.getRecipientType() == NoticeRecipientType.BOTH)
                && request.getParentId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a parent account for this private notice");
        if (request.getVisibility() == NoticeVisibility.PRIVATE
                && request.getRecipientType() == NoticeRecipientType.STUDENT && request.getParentId() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student-only notices cannot target a parent account");
        if (request.getExpiresAt() != null && !request.getExpiresAt().isAfter(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expiry must be in the future");

        Student target = null;
        ParentAccount parentTarget = null;
        if (request.getVisibility() == NoticeVisibility.PRIVATE) {
            target = students.findById(request.getStudentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
            if (target.getCoachingName() == null || !target.getCoachingName().equalsIgnoreCase(coachingName))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
            if (request.getParentId() != null) {
                parentTarget = parents.findByParentIdAndStudentId(request.getParentId(), target.getStudentId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent account not found for this student"));
                if (parentTarget.getCoachingName() != null && !parentTarget.getCoachingName().equalsIgnoreCase(coachingName))
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent account not found for this student");
            }
        }
        Notice notice = notices.save(Notice.builder().coachingName(coachingName)
                .title(request.getTitle().trim()).content(request.getContent().trim())
                .visibility(request.getVisibility()).recipientType(request.getRecipientType())
                .targetStudentId(target == null ? null : target.getStudentId())
                .targetParentId(parentTarget == null ? null : parentTarget.getParentId())
                .targetParentName(parentTarget == null ? null : parentTarget.getParentName())
                .targetStudentName(target == null ? null : target.getName() + " " + target.getSurname())
                .createdAt(LocalDateTime.now()).expiresAt(request.getExpiresAt()).build());
        return NoticeView.from(notice);
    }

    @Transactional(readOnly = true)
    public List<NoticeView> forCoaching(String coachingName) {
        return notices.findAllByCoachingNameIgnoreCaseOrderByCreatedAtDesc(coachingName)
                .stream().map(NoticeView::from).toList();
    }

    @Transactional(readOnly = true)
    public List<NoticeView> forStudentOrParent(Long studentId, Long parentId, String role) {
        Student student = students.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        NoticeRecipientType audience = "PARENT".equals(role) ? NoticeRecipientType.PARENT : NoticeRecipientType.STUDENT;
        LocalDateTime now = LocalDateTime.now();
        return notices.findAllByCoachingNameIgnoreCaseOrderByCreatedAtDesc(student.getCoachingName()).stream()
                .filter(n -> n.getExpiresAt() == null || n.getExpiresAt().isAfter(now))
                .filter(n -> n.getVisibility() == NoticeVisibility.PUBLIC
                        || (studentId.equals(n.getTargetStudentId())
                        && (n.getRecipientType() == audience || n.getRecipientType() == NoticeRecipientType.BOTH)
                        && (!"PARENT".equals(role) || (parentId != null && parentId.equals(n.getTargetParentId())))))
                .map(NoticeView::forRecipient).toList();
    }
}
