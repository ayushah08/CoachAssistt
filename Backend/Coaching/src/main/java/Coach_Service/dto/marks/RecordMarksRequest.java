package Coach_Service.dto.marks;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class RecordMarksRequest {
    @NotBlank @Size(max = 120) private String assessmentName;
    @NotBlank @Size(max = 80) private String subject;
    @NotNull @DecimalMin("0.00") private BigDecimal obtainedMarks;
    @NotNull @DecimalMin("0.01") private BigDecimal totalMarks;
    @NotNull private LocalDate assessmentDate;
    @Size(max = 500) private String remarks;
}
