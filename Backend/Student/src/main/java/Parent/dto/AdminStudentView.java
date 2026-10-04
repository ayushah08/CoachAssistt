package Parent.dto;

import Parent.entity.Student;
import java.time.LocalDateTime;

public record AdminStudentView(Long studentId, String name, String surname, String coachingName,
        String email, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static AdminStudentView from(Student s) {
        return new AdminStudentView(s.getStudentId(), s.getName(), s.getSurname(), s.getCoachingName(),
                s.getEmail(), s.getCreatedAt(), s.getUpdatedAt());
    }
}
