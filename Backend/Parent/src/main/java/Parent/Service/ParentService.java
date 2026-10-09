package Parent.Service;

import Parent.Exception.GlobalExceptionHandler;
import Parent.dto.LoginRequest;
import Parent.dto.Request;
import Parent.dto.Response;
import Parent.dto.ParentRecipientView;
import Parent.entity.Parent;
import Parent.entity.Role;
import Parent.repository.ParentRepository;
import Parent.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Value("${app.student-service-url:https://student-gzyr.onrender.com}")
    private String studentServiceUrl;

    @Transactional
    public ResponseEntity<Response> register(Request request, String authorization) {
        Boolean linked = restClient.get()
                .uri(studentServiceUrl + "/student/{id}/belongs-to-coaching?coachingName={name}",
                        request.getStudentId(), request.getCoachingName())
                .header("Authorization", authorization)
                .retrieve().body(Boolean.class);

        if (!Boolean.TRUE.equals(linked)) {
            throw new GlobalExceptionHandler.ResourceNotFoundException("Student does not belong to this coaching");
        }

        if (parentRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new GlobalExceptionHandler.BadRequestException( "A parent account already uses this email");
        }
        Parent parent = parentRepository.save(Parent.builder()
                .studentId(request.getStudentId())
                .parentName(request.getName().trim())
                .studentName(request.getStudentName())
                .coachingName(request.getCoachingName())
                        .role(Role.PARENT)
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        return new ResponseEntity<>(Response.builder().message("Parent account created successfully")
                .parentId(parent.getParentId()).studentId(parent.getStudentId())
                .parentName(parent.getParentName()).role("PARENT").build() , HttpStatus.CREATED);
    }

    public ResponseEntity<Response> login(LoginRequest request) {

        Parent parent = parentRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new GlobalExceptionHandler.BadRequestException("Invalid email"));

        if (!passwordEncoder.matches(request.getPassword(), parent.getPassword())) {
            throw new GlobalExceptionHandler.BadRequestException( "Invalid  password");
        }
        return new ResponseEntity<>(Response.builder().message("Login successful")
                .parentId(parent.getParentId()).studentId(parent.getStudentId())
                .parentName(parent.getParentName())
                .token(tokens.issue(parent.getEmail(), parent.getParentId(), parent.getStudentId()))
                .role("PARENT").build() , HttpStatus.FOUND);
    }

    public ResponseEntity<List<ParentRecipientView> >recipients(Long studentId, String coachingName) {

        return new ResponseEntity<>(parentRepository.findAllByStudentIdAndCoachingNameIgnoreCaseOrderByParentNameAsc(studentId, coachingName)
                .stream().map(p -> new ParentRecipientView(p.getParentId(), p.getStudentId(),
                        p.getParentName(), p.getEmail())).toList() , HttpStatus.FOUND);
    }
}
