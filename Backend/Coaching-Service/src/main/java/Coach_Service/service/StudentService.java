package Coach_Service.service;

import Coach_Service.dto.student.StudentRequest;
import Coach_Service.dto.student.StudentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final RestClient restClient;
    public StudentResponse createStudent(StudentRequest studentRequest) {

        return restClient.post()
                 .uri("http://localhost:8082/student/register")
                 .body(studentRequest)
                 .accept(MediaType.APPLICATION_JSON)
                 .contentType(MediaType.APPLICATION_JSON)
                 .retrieve()
                 .body(StudentResponse.class);

    }


    public String removeStudent(String studentCode) {

        return restClient.delete()
                .uri("http://localhost:8082/student/remove/" + studentCode)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
    }
}
