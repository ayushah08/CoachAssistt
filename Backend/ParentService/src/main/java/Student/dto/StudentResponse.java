package Student.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StudentResponse {

    private String StudentName;
    private Long StudentCode;
}
