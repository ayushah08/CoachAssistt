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
    public ResponseEntity<StudentResponse> createStudent(
            StudentRequest request,
            String authorization) {

        try {
            return restClient.post()
                    .uri(studentServiceUrl + "/student/register")
                    .header("Authorization", authorization)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toEntity(StudentResponse.class);

        } catch (org.springframework.web.client.RestClientResponseException ex) {
            System.err.println(
                    "Student Service HTTP status: " + ex.getStatusCode());
            System.err.println(
                    "Student Service error body: "
                            + ex.getResponseBodyAsString());
            throw ex;

        } catch (org.springframework.http.converter.HttpMessageConversionException ex) {
            System.err.println(
                    "Student Service response conversion failed: "
                            + ex.getMessage());
            throw ex;
        }
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