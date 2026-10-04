package Coach_Service.dto.parents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class Response {
    private String message;
    private Long parentId;
    private Long studentId;
    private String parentName;
    private String token;
    private String role;
}
