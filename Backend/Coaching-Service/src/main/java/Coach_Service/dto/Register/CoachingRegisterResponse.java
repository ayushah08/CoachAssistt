package Coach_Service.dto.Register;

import lombok.Builder;

import java.util.UUID;


@Builder
public class CoachingRegisterResponse {

    private String Message;
    private String coachingName;

    private String coaching_Email;

    private String CoachingOwnerName;

    private Long coachingId;
}
