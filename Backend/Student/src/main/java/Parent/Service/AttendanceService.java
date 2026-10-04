package Parent.Service;

import Parent.dto.attendance.AttendanceEntry;
import Parent.dto.attendance.AttendanceSummary;
import Parent.dto.attendance.MarkAttendanceRequest;
import Parent.entity.AttendanceRecord;
import Parent.entity.AttendanceStatus;
import Parent.entity.Student;
import Parent.repository.AttendanceRepository;
import Parent.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public AttendanceEntry mark(Long studentId, String coachingName, MarkAttendanceRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        if (student.getCoachingName() == null || !student.getCoachingName().equalsIgnoreCase(coachingName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
        if (request.getDate().isAfter(LocalDate.now(ZoneId.of("Asia/Kolkata")))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attendance date cannot be in the future");
        }

        AttendanceRecord record = attendanceRepository
                .findByStudentIdAndAttendanceDate(studentId, request.getDate())
                .orElseGet(() -> AttendanceRecord.builder()
                        .studentId(studentId)
                        .attendanceDate(request.getDate())
                        .build());
        record.setStatus(request.getStatus());
        record.setMarkedBy(coachingName);
        record.setMarkedAt(LocalDateTime.now());
        AttendanceRecord saved = attendanceRepository.save(record);
        return toEntry(saved);
    }

    @Transactional(readOnly = true)
    public AttendanceSummary getSummary(Long studentId) {
        List<AttendanceRecord> records = attendanceRepository
                .findAllByStudentIdOrderByAttendanceDateDesc(studentId);
        long presentDays = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.PRESENT);
        long totalDays = records.size();
        double percentage = totalDays == 0 ? 0.0 : Math.round(presentDays * 10000.0 / totalDays) / 100.0;
        return new AttendanceSummary(studentId, totalDays, presentDays, totalDays - presentDays, percentage,
                records.stream().map(this::toEntry).toList());
    }

    private AttendanceEntry toEntry(AttendanceRecord record) {
        return new AttendanceEntry(record.getStudentId(), record.getAttendanceDate(), record.getStatus(),
                record.getMarkedBy(), record.getMarkedAt());
    }
}
