package Coach_Service.dto.student;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentRequest {

    private String coaching_name;
    private String student_name;
    private Long studentCode;
}
