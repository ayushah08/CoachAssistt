package Coach_Service.controller;

import Coach_Service.dto.Register.CoachingRegisterRequest;
import Coach_Service.dto.student.StudentRequest;
import Coach_Service.dto.student.StudentResponse;
import Coach_Service.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/register")
    public StudentResponse registerStudent(@RequestBody StudentRequest studentRequest){
        return studentService.createStudent(studentRequest);

    }

    @DeleteMapping("/remove/{studentCode}")
    public String removeStudent(@PathVariable String studentCode){
        return studentService.removeStudent(studentCode);
    }
}
