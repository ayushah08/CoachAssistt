package Coach_Service.dto.student;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class StudentRequest {
    @NotBlank @JsonAlias("CoachingName")
    private String coachingName;
    @NotBlank @JsonAlias("FirstName")
    private String firstName;
    @NotBlank
    private String surname;
    @Email @NotBlank
    private String email;
    @NotBlank @Size(min = 8, max = 72)
    private String password;
}
