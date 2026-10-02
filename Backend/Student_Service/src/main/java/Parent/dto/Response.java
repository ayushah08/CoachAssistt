package Parent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Response {
    private String message;
    private Long parentId;
    private Long studentId;
    private String parentName;
    private String token;
    private String role;
}
