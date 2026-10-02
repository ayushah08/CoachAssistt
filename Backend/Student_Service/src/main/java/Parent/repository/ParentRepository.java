package Parent.repository;

import Parent.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ParentRepository extends JpaRepository<Parent, Long> {
    Optional<Parent> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    List<Parent> findAllByStudentIdAndCoachingNameIgnoreCaseOrderByParentNameAsc(Long studentId, String coachingName);
}
