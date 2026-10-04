package Coach_Service.service;

import Coach_Service.dto.marks.MarkEntry;
import Coach_Service.dto.marks.MarksSummary;
import Coach_Service.dto.marks.RecordMarksRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;

@Service @RequiredArgsConstructor
public class MarksService {
    private final RestClient restClient;
    @Value("${app.student-service-url}") private String studentServiceUrl;

    public List<MarksSummary> students(String authorization) {
        MarksSummary[] rows = restClient.get().uri(studentServiceUrl + "/marks/students")
                .header("Authorization", authorization).retrieve().body(MarksSummary[].class);
        return rows == null ? List.of() : List.of(rows);
    }

    public MarksSummary student(Long studentId, String authorization) {
        return restClient.get().uri(studentServiceUrl + "/marks/students/{studentId}", studentId)
                .header("Authorization", authorization).retrieve().body(MarksSummary.class);
    }

    public MarkEntry add(Long studentId, RecordMarksRequest request, String authorization) {
        return restClient.post().uri(studentServiceUrl + "/marks/students/{studentId}", studentId)
                .header("Authorization", authorization).contentType(MediaType.APPLICATION_JSON)
                .body(request).retrieve().body(MarkEntry.class);
    }

    public MarksSummary me(String authorization) {
        return restClient.get().uri(studentServiceUrl + "/marks/me")
                .header("Authorization", authorization).retrieve().body(MarksSummary.class);
    }
}
