package Student.controller;

import Student.Service.NoticeService;
import Student.dto.notices.CreateNoticeRequest;
import Student.dto.notices.NoticeView;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController @RequestMapping("/notices") @RequiredArgsConstructor
public class NoticeController {
    private final NoticeService service;

    @GetMapping
    public List<NoticeView> coachingNotices(Authentication authentication) {
        Claims claims = claims(authentication);
        requireRole(claims, "COACHING");
        String coachingName = claims.get("coachingName", String.class);
        if (coachingName == null || coachingName.isBlank()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Coaching identity is missing");
        return service.forCoaching(coachingName);
    }

    @PostMapping
    public ResponseEntity<NoticeView> create(Authentication authentication,
            @Valid @RequestBody CreateNoticeRequest request) {
        Claims claims = claims(authentication);
        requireRole(claims, "COACHING");
        String coachingName = claims.get("coachingName", String.class);
        if (coachingName == null || coachingName.isBlank()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Coaching identity is missing");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(coachingName, request));
    }

    @GetMapping("/me")
    public List<NoticeView> myNotices(Authentication authentication) {
        Claims claims = claims(authentication);
        String role = claims.get("role", String.class);
        Long studentId;
        if ("STUDENT".equals(role)) studentId = claims.get("userId", Long.class);
        else if ("PARENT".equals(role)) studentId = claims.get("studentId", Long.class);
        else throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student or parent access is required");
        if (studentId == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student identity is missing");
        Long parentId = "PARENT".equals(role) ? claims.get("userId", Long.class) : null;
        return service.forStudentOrParent(studentId, parentId, role);
    }

    private void requireRole(Claims claims, String role) {
        if (!role.equals(claims.get("role", String.class))) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Coaching access is required");
    }

    private Claims claims(Authentication authentication) {
        if (authentication != null && authentication.getDetails() instanceof Claims claims) return claims;
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A valid bearer token is required");
    }
}
