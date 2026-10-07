package Student.controller;

import Student.Exception.GlobalExceptionHandler;
import Student.Service.StudentService;
import Student.dto.StudentRequest;
import Student.dto.StudentResponse;
import Student.dto.StudentLoginRequest;
import Student.dto.AuthResponse;
import Student.dto.AdminStudentView;
import Student.repository.StudentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentRepository studentRepository;

    @GetMapping("/admin/students")
    public ResponseEntity<List<AdminStudentView>> adminStudents() {
        return new ResponseEntity<>(studentRepository.findAll().stream().map(AdminStudentView::from).toList() , HttpStatus.OK);
    }

    @GetMapping("/admin/students/{id}")
    public ResponseEntity<Optional<AdminStudentView >> adminStudent(@PathVariable Long id) {

      AdminStudentView adminStudentView = studentRepository.findById(id).map(AdminStudentView::from).orElse(null);

      if(adminStudentView == null) {
           throw new GlobalExceptionHandler.ResourceNotFoundException(" Student Not Found");
      }
      return new ResponseEntity<>(Optional.of(adminStudentView), HttpStatus.FOUND);
    }

    @PostMapping("/register")
    public ResponseEntity<StudentResponse> registerStudent(@Valid @RequestBody StudentRequest student,
            Authentication authentication) {

        student.setCoachingName(coachingName(authentication));
        return studentService.createStudent(student);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody StudentLoginRequest request) {
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
