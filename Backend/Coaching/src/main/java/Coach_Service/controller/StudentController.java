package Coach_Service.controller;

import Coach_Service.dto.Login.CoachingLoginRequest;
import Coach_Service.dto.Login.LoginResponse;
import Coach_Service.dto.student.StudentRequest;
import Coach_Service.dto.student.StudentResponse;
import Coach_Service.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @PostMapping("/create")
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(request, authorization));
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody CoachingLoginRequest request) {
        return studentService.login(request);
    }

    @DeleteMapping("/{studentCode}")
    public ResponseEntity<Void> remove(@PathVariable String studentCode,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        studentService.removeStudent(studentCode, authorization);
        return ResponseEntity.noContent().build();
    }
}
