package Parent.Service;

import Parent.dto.AuthResponse;
import Parent.dto.StudentLoginRequest;
import Parent.dto.StudentRequest;
import Parent.dto.StudentResponse;
import Parent.entity.Student;
import Parent.repository.StudentRepository;
import Parent.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokens;

    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        if (studentRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A student account already uses this email");
        }
        Student student = studentRepository.save(Student.builder()
                .name(request.getFirstName().trim())
                .surname(request.getSurname().trim())
                .coachingName(request.getCoachingName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        return StudentResponse.builder()
                .studentName(student.getName() + " " + student.getSurname())
                .studentCode(student.getStudentId())
                .build();
    }

    public AuthResponse login(StudentLoginRequest request) {
        Student student = studentRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return new AuthResponse("Login successful", tokens.issue(student.getEmail(), student.getStudentId()),
                student.getStudentId(), "STUDENT");
    }

    @Transactional
    public void removeStudent(Long studentId, String coachingName) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        if (!student.getCoachingName().equalsIgnoreCase(coachingName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
        studentRepository.delete(student);
    }

    public boolean belongsToCoaching(Long studentId, String coachingName) {
        return studentRepository.existsByStudentIdAndCoachingNameIgnoreCase(studentId, coachingName);
    }
}
