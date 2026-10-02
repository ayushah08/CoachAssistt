package Coach_Service.service;

import Coach_Service.dto.Login.CoachingLoginRequest;
import Coach_Service.dto.Login.LoginResponse;
import Coach_Service.dto.student.StudentRequest;
import Coach_Service.dto.student.StudentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final RestClient restClient;
    @Value("${app.student-service-url}")
    private String studentServiceUrl;

    public StudentResponse createStudent(StudentRequest request, String authorization) {
        return restClient.post().uri(studentServiceUrl + "/student/register")
                .header("Authorization", authorization).body(request)
                .contentType(MediaType.APPLICATION_JSON).retrieve().body(StudentResponse.class);
    }

    public LoginResponse login(CoachingLoginRequest request) {
        return restClient.post().uri(studentServiceUrl + "/student/login").body(request)
                .contentType(MediaType.APPLICATION_JSON).retrieve().body(LoginResponse.class);
    }

    public String removeStudent(String studentCode, String authorization) {
        return restClient.delete().uri(studentServiceUrl + "/student/remove/{id}", studentCode)
                .header("Authorization", authorization).retrieve().body(String.class);
    }
}
