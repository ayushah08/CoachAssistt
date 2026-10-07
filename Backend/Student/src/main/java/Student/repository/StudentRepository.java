package Student.repository;

import Student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByStudentIdAndCoachingNameIgnoreCase(Long studentId, String coachingName);

    List<Student> findAllByCoachingNameIgnoreCaseOrderBySurnameAscNameAsc(String coachingName);
}
