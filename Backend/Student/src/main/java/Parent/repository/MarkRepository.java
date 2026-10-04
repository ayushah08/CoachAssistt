package Parent.repository;

import Parent.entity.MarkRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MarkRepository extends JpaRepository<MarkRecord, Long> {
    List<MarkRecord> findAllByStudentIdOrderByAssessmentDateDescIdDesc(Long studentId);
}
