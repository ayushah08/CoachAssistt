package Coach_Service.dto.Register;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CoachingRegisterRequest {

    @NotBlank
    private String coachingName;


    @NotBlank
    private String email;
    @NotBlank
    private String Coaching_Address;

    @NotBlank
    private String CoachingOwnerName;

    private String password;

}
