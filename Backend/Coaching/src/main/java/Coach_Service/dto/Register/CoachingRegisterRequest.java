package Coach_Service.dto.Register;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoachingRegisterRequest {
    @NotBlank
    private String coachingName;
    @Email
    @NotBlank
    private String email;
    @JsonAlias("Coaching_Address")
    @NotBlank
    private String coachingAddress;
    @JsonAlias("CoachingOwnerName")
    @NotBlank
    private String coachingOwnerName;
    @NotBlank
    @Size(min = 8, max = 72)
    private String password;
}
