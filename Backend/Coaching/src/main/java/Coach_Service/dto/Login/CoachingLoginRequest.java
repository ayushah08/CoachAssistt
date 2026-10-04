package Coach_Service.dto.Login;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoachingLoginRequest {
    @Email
    @NotBlank
    @JsonAlias({"Coaching_Email", "coachingEmail"})
    private String email;
    @NotBlank
    private String password;
}
