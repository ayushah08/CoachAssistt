package Admin.dto;

import Admin.entity.CoachingAccount;
import java.time.LocalDateTime;

public record CoachingAdminView(Long coachingId, String coachingName, String address,
        String email, String ownerName, boolean approved, LocalDateTime requestDate) {
    public static CoachingAdminView from(CoachingAccount c) {
        return new CoachingAdminView(c.getCoachingId(), c.getCoachingName(), c.getCoachingAddress(),
                c.getCoachingEmail(), c.getCoachingOwnerName(), c.isVerified(), c.getDateTime());
    }
}
