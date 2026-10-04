package Coach_Service.dto.Register;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CoachingRegisterResponse {
    private String message;
    private String coachingName;
    private String email;
    private String ownerName;
    private Long coachingId;
}
