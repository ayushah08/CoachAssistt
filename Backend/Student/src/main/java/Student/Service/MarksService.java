package Student.Service;

import Student.dto.marks.MarkEntry;
import Student.dto.marks.MarksSummary;
import Student.dto.marks.RecordMarksRequest;
import Student.entity.MarkRecord;
import Student.entity.Student;
import Student.repository.MarkRepository;
import Student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service @RequiredArgsConstructor
public class MarksService {
    private final MarkRepository marks;
    private final StudentRepository students;

    @Transactional
    public MarkEntry add(Long studentId, String coachingName, RecordMarksRequest request) {
        Student student = findStudent(studentId);
        ensureCoachingOwns(student, coachingName);
        validateMarks(request);
        if (request.getAssessmentDate().isAfter(java.time.LocalDate.now(ZoneId.of("Asia/Kolkata"))))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assessment date cannot be in the future");
        MarkRecord saved = marks.save(MarkRecord.builder().studentId(studentId)
                .coachingName(student.getCoachingName()).assessmentName(request.getAssessmentName().trim())
                .subject(request.getSubject().trim()).obtainedMarks(request.getObtainedMarks())
                .totalMarks(request.getTotalMarks()).assessmentDate(request.getAssessmentDate())
                .remarks(request.getRemarks() == null ? null : request.getRemarks().trim())
                .markedBy(coachingName).markedAt(LocalDateTime.now()).build());
        return MarkEntry.from(saved);
    }

    @Transactional(readOnly = true)
    public MarksSummary forStudentAsCoaching(Long studentId, String coachingName) {
        Student student = findStudent(studentId);
        ensureCoachingOwns(student, coachingName);
        return summary(student);
    }

    @Transactional(readOnly = true)
    public List<MarksSummary> forCoaching(String coachingName) {
        return students.findAllByCoachingNameIgnoreCaseOrderBySurnameAscNameAsc(coachingName)
                .stream().map(this::summary).toList();
    }

    @Transactional(readOnly = true)
    public MarksSummary forLinkedUser(Long studentId) {
        if (studentId == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student identity is missing");
        return summary(findStudent(studentId));
    }

    private Student findStudent(Long studentId) {
        return students.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }

    private void ensureCoachingOwns(Student student, String coachingName) {
        if (coachingName == null || student.getCoachingName() == null
                || !student.getCoachingName().equalsIgnoreCase(coachingName))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
    }

    private void validateMarks(RecordMarksRequest request) {
        if (request.getObtainedMarks() != null && request.getTotalMarks() != null
                && request.getObtainedMarks().compareTo(request.getTotalMarks()) > 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Obtained marks cannot exceed total marks");
    }

    private MarksSummary summary(Student student) {
        List<MarkEntry> records = marks.findAllByStudentIdOrderByAssessmentDateDescIdDesc(student.getStudentId())
                .stream().map(MarkEntry::from).toList();
        BigDecimal obtained = records.stream().map(MarkEntry::obtainedMarks).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal possible = records.stream().map(MarkEntry::totalMarks).reduce(BigDecimal.ZERO, BigDecimal::add);
        double percentage = possible.signum() == 0 ? 0.0 : obtained.multiply(BigDecimal.valueOf(100))
                .divide(possible, 2, RoundingMode.HALF_UP).doubleValue();
        return new MarksSummary(student.getStudentId(), student.getName() + " " + student.getSurname(),
                obtained, possible, percentage, records);
    }
}
