package Admin.dto;

import java.time.LocalDateTime;

public record StudentAdminView(Long studentId, String name, String surname,
        String coachingName, String email, LocalDateTime createdAt, LocalDateTime updatedAt) {}
