package Student.repository;

import Student.entity.ParentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ParentAccountRepository extends JpaRepository<ParentAccount, Long> {
    Optional<ParentAccount> findByParentIdAndStudentId(Long parentId, Long studentId);
}
