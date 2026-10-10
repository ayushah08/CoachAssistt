package Student.Service;

import Student.Exception.GlobalExceptionHandler;
import Student.dto.AuthResponse;
import Student.dto.StudentLoginRequest;
import Student.dto.StudentRequest;
import Student.dto.StudentResponse;
import Student.entity.Role;
import Student.entity.Student;
import Student.repository.StudentRepository;
import Student.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokens;

    @Transactional
    public ResponseEntity<StudentResponse> createStudent(StudentRequest request) {

        if (studentRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new GlobalExceptionHandler.BadRequestException("A student account already uses this email");
        }
        Student student = studentRepository.save(Student.builder()
                .name(request.getFirstName().trim())
                .surname(request.getSurname().trim())
                .coachingName(request.getCoachingName().trim())
                .role(Role.STUDENT)
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());


        return new ResponseEntity<>(StudentResponse.builder()
                .studentName(student.getUsername())
                .studentCode(student.getStudentId())
                .build(), HttpStatus.CREATED);
    }

    public ResponseEntity<AuthResponse> login(StudentLoginRequest request) {

        Student student = studentRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new GlobalExceptionHandler.BadRequestException("Invalid email"));

        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            throw new GlobalExceptionHandler.BadRequestException("Invalid password");
        }

        return new ResponseEntity<>(
                new AuthResponse(
                        "Login successful",
                        tokens.issue(student.getEmail(), student.getStudentId()),
                        student.getStudentId(),
                        "STUDENT"
                ),
                HttpStatus.OK // <--- Use HttpStatus.OK (200)
        );
    }

    @Transactional
    public ResponseEntity<String> removeStudent(Long studentId, String coachingName) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("Student not found with id " + studentId));

        if (!student.getCoachingName().equalsIgnoreCase(coachingName)) {
            throw new GlobalExceptionHandler.ResourceNotFoundException("Student not found with id " + studentId);
        }

        studentRepository.delete(student);

        return new ResponseEntity<>("Student Deleted Successfully", HttpStatus.OK);
    }

    public boolean belongsToCoaching(Long studentId, String coachingName) {
        return studentRepository.existsByStudentIdAndCoachingNameIgnoreCase(studentId, coachingName);
    }
}
