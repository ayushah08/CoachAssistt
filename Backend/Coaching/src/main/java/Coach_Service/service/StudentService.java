package Coach_Service.service;

import Coach_Service.dto.Login.CoachingLoginRequest;
import Coach_Service.dto.Login.LoginResponse;
import Coach_Service.dto.student.StudentRequest;
import Coach_Service.dto.student.StudentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final RestClient restClient;

    @Value("${app.student-service-url}")
    private String studentServiceUrl;

    public ResponseEntity<StudentResponse> createStudent(StudentRequest request, String authorization) {

        return restClient.post()
                .uri(studentServiceUrl + "/student/register")
                .header("Authorization", authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    String errorBody = new String(resp.getBody().readAllBytes());
                    throw new HttpClientErrorException(resp.getStatusCode(), errorBody);
                })
                .toEntity(StudentResponse.class);
    }

    public ResponseEntity<LoginResponse> login(CoachingLoginRequest request) {
        return restClient.post()
                .uri(studentServiceUrl + "/student/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    String errorBody = new String(resp.getBody().readAllBytes());
                    throw new HttpClientErrorException(resp.getStatusCode(), errorBody);
                })
                .toEntity(LoginResponse.class);
    }

    public ResponseEntity<Void> removeStudent(String studentCode, String authorization) {
        return restClient.delete()
                .uri(studentServiceUrl + "/student/remove/{id}", studentCode)
                .header("Authorization", authorization)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    String errorBody = new String(resp.getBody().readAllBytes());
                    throw new HttpClientErrorException(resp.getStatusCode(), errorBody);
                })
                .toEntity(Void.class);
    }
}