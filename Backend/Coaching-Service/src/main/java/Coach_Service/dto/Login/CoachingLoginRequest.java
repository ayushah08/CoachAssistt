package Coach_Service.dto.Login;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CoachingLoginRequest {

    @NotBlank
    private String Coaching_Name;
    @NotBlank
    private String Coaching_Email;
}
