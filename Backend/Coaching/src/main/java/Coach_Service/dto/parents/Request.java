package Coach_Service.dto.parents;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class Request {
    @NotBlank
    private String name;
    @NotNull
    private Long studentId;
    @JsonAlias("StudentName")
    private String studentName;
    @JsonAlias("CoachingName")
    private String coachingName;
    @Email @NotBlank
    private String email;
    @NotBlank @Size(min = 8, max = 72)
    private String password;
}
