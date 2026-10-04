package Coach_Service.controller;

import Coach_Service.dto.attendance.AttendanceEntry;
import Coach_Service.dto.attendance.AttendanceSummary;
import Coach_Service.dto.attendance.MarkAttendanceRequest;
import Coach_Service.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping("/students/{studentId}")
    public ResponseEntity<AttendanceEntry> mark(@PathVariable Long studentId,
            @Valid @RequestBody MarkAttendanceRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseEntity.ok(attendanceService.mark(studentId, request, authorization));
    }

    @GetMapping("/me")
    public AttendanceSummary myAttendance(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return attendanceService.myAttendance(authorization);
    }
}
