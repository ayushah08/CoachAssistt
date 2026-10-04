package Parent.controller;

import Parent.Service.StudentService;
import Parent.dto.StudentRequest;
import Parent.dto.StudentResponse;
import Parent.dto.StudentLoginRequest;
import Parent.dto.AuthResponse;
import Parent.dto.AdminStudentView;
import Parent.repository.StudentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import io.jsonwebtoken.Claims;
import java.util.List;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentRepository studentRepository;

    @GetMapping("/admin/students")
    public List<AdminStudentView> adminStudents() {
        return studentRepository.findAll().stream().map(AdminStudentView::from).toList();
    }

    @GetMapping("/admin/students/{id}")
    public AdminStudentView adminStudent(@PathVariable Long id) {
        return studentRepository.findById(id).map(AdminStudentView::from)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }

    @PostMapping("/register")
    public ResponseEntity<StudentResponse> registerStudent(@Valid @RequestBody StudentRequest student,
            Authentication authentication) {
        student.setCoachingName(coachingName(authentication));
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(student));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody StudentLoginRequest request) {
        return studentService.login(request);
    }

    @DeleteMapping("/remove/{studentCode}")
    public ResponseEntity<Void> removeStudent(@PathVariable Long studentCode, Authentication authentication) {
        studentService.removeStudent(studentCode, coachingName(authentication));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{studentId}/belongs-to-coaching")
    public boolean belongsToCoaching(@PathVariable Long studentId, @RequestParam String coachingName,
            Authentication authentication) {
        String authenticatedCoachingName = coachingName(authentication);
        return authenticatedCoachingName.equalsIgnoreCase(coachingName)
                && studentService.belongsToCoaching(studentId, authenticatedCoachingName);
    }

    private String coachingName(Authentication authentication) {
        if (authentication != null && authentication.getDetails() instanceof Claims claims) {
            String coachingName = claims.get("coachingName", String.class);
            if (coachingName != null && !coachingName.isBlank()) return coachingName;
        }
        throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN, "Coaching identity is missing");
    }
}
