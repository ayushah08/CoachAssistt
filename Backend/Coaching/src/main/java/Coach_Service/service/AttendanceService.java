package Coach_Service.service;

import Coach_Service.dto.attendance.AttendanceEntry;
import Coach_Service.dto.attendance.AttendanceSummary;
import Coach_Service.dto.attendance.MarkAttendanceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final RestClient restClient;
    @Value("${app.student-service-url}")
    private String studentServiceUrl;

    public AttendanceEntry mark(Long studentId, MarkAttendanceRequest request, String authorization) {
        return restClient.post()
                .uri(studentServiceUrl + "/attendance/students/{studentId}", studentId)
                .header("Authorization", authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve().body(AttendanceEntry.class);
    }

    public AttendanceSummary myAttendance(String authorization) {
        return restClient.get().uri(studentServiceUrl + "/attendance/me")
                .header("Authorization", authorization)
                .retrieve().body(AttendanceSummary.class);
    }
}
