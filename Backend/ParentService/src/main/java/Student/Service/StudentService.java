package Student.Service;

import Student.dto.StudentRequest;
import Student.dto.StudentResponse;
import Student.entity.Student;
import Student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class StudentService {


    private final StudentRepository studentRepository;

    public ResponseEntity<StudentResponse> registerStudent(StudentRequest student) {

        Student student1 = Student.builder().name(student.getFirstName()).surname(student.getSurname()).coachingName(student.getCoachingName()).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        studentRepository.save(student1);

        StudentResponse studentResponse = StudentResponse.builder().StudentName(student1.getName() + " " +  student1.getSurname()).StudentCode(student1.getStudentId()).build();

        return new ResponseEntity<>(studentResponse, HttpStatus.CREATED);
    }

    public ResponseEntity<Void> removeStudent(Long studentCode) {

        Student student = studentRepository.findById(studentCode)
                .orElseThrow(() -> new NoSuchElementException("Student not found"));
        studentRepository.delete(student);
        return ResponseEntity.noContent().build();
    }
}
