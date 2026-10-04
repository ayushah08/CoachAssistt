package Parent.controller;

import Parent.Service.MarksService;
import Parent.dto.marks.MarkEntry;
import Parent.dto.marks.MarksSummary;
import Parent.dto.marks.RecordMarksRequest;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController @RequestMapping("/marks") @RequiredArgsConstructor
public class MarksController {
    private final MarksService service;

    @GetMapping("/students")
    public List<MarksSummary> coachingStudents(Authentication authentication) {
        Claims claims = claims(authentication);
        requireRole(claims, "COACHING");
        return service.forCoaching(coachingName(claims));
    }

    @GetMapping("/students/{studentId}")
    public MarksSummary coachingStudent(@PathVariable Long studentId, Authentication authentication) {
        Claims claims = claims(authentication);
        requireRole(claims, "COACHING");
        return service.forStudentAsCoaching(studentId, coachingName(claims));
    }

    @PostMapping("/students/{studentId}")
    public ResponseEntity<MarkEntry> record(@PathVariable Long studentId,
            @Valid @RequestBody RecordMarksRequest request, Authentication authentication) {
        Claims claims = claims(authentication);
        requireRole(claims, "COACHING");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(studentId, coachingName(claims), request));
    }

    @GetMapping("/me")
    public MarksSummary myMarks(Authentication authentication) {
        Claims claims = claims(authentication);
        String role = claims.get("role", String.class);
        Long studentId;
        if ("STUDENT".equals(role)) studentId = claims.get("userId", Long.class);
        else if ("PARENT".equals(role)) studentId = claims.get("studentId", Long.class);
        else throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student or parent access is required");
        return service.forLinkedUser(studentId);
    }

    private String coachingName(Claims claims) {
        String name = claims.get("coachingName", String.class);
        if (name == null || name.isBlank()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Coaching identity is missing");
        return name;
    }

    private void requireRole(Claims claims, String role) {
        if (!role.equals(claims.get("role", String.class))) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Coaching access is required");
    }

    private Claims claims(Authentication authentication) {
        if (authentication != null && authentication.getDetails() instanceof Claims claims) return claims;
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A valid bearer token is required");
    }
}
