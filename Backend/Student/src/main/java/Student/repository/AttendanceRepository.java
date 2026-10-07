package Student.repository;

import Student.entity.AttendanceRecord;
import Student.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    Optional<AttendanceRecord> findByStudentIdAndAttendanceDate(Long studentId, LocalDate attendanceDate);
    List<AttendanceRecord> findAllByStudentIdOrderByAttendanceDateDesc(Long studentId);
    long countByStudentIdAndStatus(Long studentId, AttendanceStatus status);
}
