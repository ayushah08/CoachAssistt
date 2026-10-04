package Coach_Service.controller;

import Coach_Service.dto.notices.CreateNoticeRequest;
import Coach_Service.dto.notices.NoticeView;
import Coach_Service.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/v1/notices") @RequiredArgsConstructor
public class NoticeController {
    private final NoticeService service;

    @GetMapping
    public List<NoticeView> listForCoaching(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return service.coachingNotices(authorization);
    }

    @PostMapping
    public ResponseEntity<NoticeView> create(@Valid @RequestBody CreateNoticeRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, authorization));
    }

    @GetMapping("/me")
    public List<NoticeView> myNotices(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return service.myNotices(authorization);
    }
}
