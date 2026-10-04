package Parent.Service;

import Parent.dto.LoginRequest;
import Parent.dto.Request;
import Parent.dto.Response;
import Parent.dto.ParentRecipientView;
import Parent.entity.Parent;
import Parent.repository.ParentRepository;
import Parent.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParentService {
    private final ParentRepository parentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokens;
    private final RestClient restClient;
    @Value("${app.student-service-url:http://localhost:8082}")
    private String studentServiceUrl;

    @Transactional
    public Response register(Request request, String authorization) {
        Boolean linked = restClient.get()
                .uri(studentServiceUrl + "/student/{id}/belongs-to-coaching?coachingName={name}",
                        request.getStudentId(), request.getCoachingName())
                .header("Authorization", authorization)
                .retrieve().body(Boolean.class);
        if (!Boolean.TRUE.equals(linked)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student does not belong to this coaching");
        }
        if (parentRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A parent account already uses this email");
        }
        Parent parent = parentRepository.save(Parent.builder()
                .studentId(request.getStudentId())
                .parentName(request.getName().trim())
                .studentName(request.getStudentName())
                .coachingName(request.getCoachingName())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        return Response.builder().message("Parent account created successfully")
                .parentId(parent.getParentId()).studentId(parent.getStudentId())
                .parentName(parent.getParentName()).role("PARENT").build();
    }

    public Response login(LoginRequest request) {
        Parent parent = parentRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), parent.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return Response.builder().message("Login successful")
                .parentId(parent.getParentId()).studentId(parent.getStudentId())
                .parentName(parent.getParentName())
                .token(tokens.issue(parent.getEmail(), parent.getParentId(), parent.getStudentId()))
                .role("PARENT").build();
    }

    public List<ParentRecipientView> recipients(Long studentId, String coachingName) {
        return parentRepository.findAllByStudentIdAndCoachingNameIgnoreCaseOrderByParentNameAsc(studentId, coachingName)
                .stream().map(p -> new ParentRecipientView(p.getParentId(), p.getStudentId(),
                        p.getParentName(), p.getEmail())).toList();
    }
}
