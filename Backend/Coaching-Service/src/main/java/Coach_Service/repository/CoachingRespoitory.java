package Coach_Service.repository;

import Coach_Service.entity.Coaching;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CoachingRespoitory extends JpaRepository<Coaching, Long> {
    Optional<Coaching> findByCoachingName(String username);

//    Coaching findByCoachingName(@NotBlank String coachingName);


    boolean existsByCoachingOwnerName(@NotBlank String coachingOwnerName);
}
