package Coach_Service.controller;

import Coach_Service.dto.marks.MarkEntry;
import Coach_Service.dto.marks.MarksSummary;
import Coach_Service.dto.marks.RecordMarksRequest;
import Coach_Service.service.MarksService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/v1/marks") @RequiredArgsConstructor
public class MarksController {
    private final MarksService marksService;

    @GetMapping("/students")
    public List<MarksSummary> students(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return marksService.students(authorization);
    }

    @GetMapping("/students/{studentId}")
    public MarksSummary student(@PathVariable Long studentId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return marksService.student(studentId, authorization);
    }

    @PostMapping("/students/{studentId}")
    public ResponseEntity<MarkEntry> add(@PathVariable Long studentId,
            @Valid @RequestBody RecordMarksRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marksService.add(studentId, request, authorization));
    }

    @GetMapping("/me")
    public MarksSummary me(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return marksService.me(authorization);
    }
}
