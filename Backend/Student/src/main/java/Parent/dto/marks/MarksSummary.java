package Parent.dto.marks;

import java.math.BigDecimal;
import java.util.List;

public record MarksSummary(Long studentId, String studentName, BigDecimal totalObtained,
        BigDecimal totalPossible, double overallPercentage, List<MarkEntry> records) {}
