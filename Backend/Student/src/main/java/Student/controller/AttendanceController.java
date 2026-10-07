package Student.controller;

import Student.Service.AttendanceService;
import Student.dto.attendance.AttendanceEntry;
import Student.dto.attendance.AttendanceSummary;
import Student.dto.attendance.MarkAttendanceRequest;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping("/students/{studentId}")
    public ResponseEntity<AttendanceEntry> mark(@PathVariable Long studentId,
            @Valid @RequestBody MarkAttendanceRequest request, Authentication authentication) {
        Claims claims = claims(authentication);
        if (!"COACHING".equals(claims.get("role", String.class))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Coaching access is required");
        }
        String coachingName = claims.get("coachingName", String.class);
        if (coachingName == null || coachingName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Coaching identity is missing");
        }
        return ResponseEntity.ok(attendanceService.mark(studentId, coachingName, request));
    }

    @GetMapping("/me")
    public AttendanceSummary myAttendance(Authentication authentication) {
        Claims claims = claims(authentication);
        String role = claims.get("role", String.class);
        Long studentId;
        if ("STUDENT".equals(role)) {
            studentId = claims.get("userId", Long.class);
        } else if ("PARENT".equals(role)) {
            studentId = claims.get("studentId", Long.class);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student or parent access is required");
        }
        if (studentId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student identity is missing");
        }
        return attendanceService.getSummary(studentId);
    }

    private Claims claims(Authentication authentication) {
        if (authentication != null && authentication.getDetails() instanceof Claims claims) return claims;
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A valid bearer token is required");
    }
}
