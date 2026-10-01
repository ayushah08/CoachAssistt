package Student.controller;

import Student.Service.StudentService;
import Student.dto.StudentRequest;
import Student.dto.StudentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/register")
    public ResponseEntity<StudentResponse> registerStudent(@RequestBody StudentRequest student) {

        return studentService.registerStudent(student);
    }

    @DeleteMapping("/remove/{studentCode}")
    public ResponseEntity<Void> removeStudent(@PathVariable Long studentCode) {
        return studentService.removeStudent(studentCode);
    }
}
