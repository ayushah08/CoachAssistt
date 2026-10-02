package Coach_Service.controller;

import Coach_Service.dto.Login.CoachingLoginRequest;
import Coach_Service.dto.parents.Request;
import Coach_Service.dto.parents.Response;
import Coach_Service.dto.parents.ParentRecipientView;
import Coach_Service.service.ParentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
public class ParentsController {
    private final ParentService parentService;
    private final Coach_Service.service.CoachingService coachingService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@Valid @RequestBody Request request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, Principal principal) {
        request.setCoachingName(coachingService.coachingNameForEmail(principal.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(parentService.register(request, authorization));
    }

    @PostMapping("/login")
    public Response login(@Valid @RequestBody CoachingLoginRequest request) {
        return parentService.login(request);
    }

    @GetMapping("/students/{studentId}/recipients")
    public java.util.List<ParentRecipientView> recipients(@PathVariable Long studentId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return parentService.recipients(studentId, authorization);
    }
}
