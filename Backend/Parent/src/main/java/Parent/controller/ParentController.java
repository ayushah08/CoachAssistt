package Parent.controller;

import Parent.Exception.GlobalExceptionHandler;
import Parent.Service.ParentService;
import Parent.dto.LoginRequest;
import Parent.dto.Request;
import Parent.dto.Response;
import Parent.dto.ParentRecipientView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.security.core.Authentication;
import io.jsonwebtoken.Claims;

import java.util.List;

@RestController
@RequestMapping("/parent")
@RequiredArgsConstructor
public class ParentController {
    private final ParentService parentService;

    @GetMapping("/students/{studentId}/recipients")
    public ResponseEntity<List<ParentRecipientView>> recipients(@PathVariable Long studentId,
                                                Authentication authentication) {

        if (authentication == null || !(authentication.getDetails() instanceof Claims claims)
                || !"COACHING".equals(claims.get("role", String.class))) {

            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "Coaching identity is required");
        }

        String coachingName = claims.get("coachingName", String.class);
        if (coachingName == null || coachingName.isBlank())

            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "Coaching identity is missing");
        return parentService.recipients(studentId, coachingName);
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@Valid @RequestBody Request request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, Authentication authentication) {

        if (authentication == null || !(authentication.getDetails() instanceof Claims claims)
                || claims.get("role", String.class) == null
                || !"COACHING".equals(claims.get("role", String.class)))
        {
            throw new GlobalExceptionHandler.BadRequestException(
                     "Coaching identity is required");
        }
        request.setCoachingName(claims.get("coachingName", String.class));

        return parentService.register(request, authorization);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@Valid @RequestBody LoginRequest request) {
        return parentService.login(request);
    }
}
