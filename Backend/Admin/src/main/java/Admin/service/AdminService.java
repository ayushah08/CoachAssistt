package Admin.service;

import Admin.dto.AdminLoginRequest;
import Admin.dto.AdminLoginResponse;
import Admin.dto.CoachingAdminView;
import Admin.dto.StudentAdminView;
import Admin.entity.AdminUser;
import Admin.entity.CoachingAccount;
import Admin.repository.AdminUserRepository;
import Admin.repository.CoachingAccountRepository;
import Admin.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service @RequiredArgsConstructor
public class AdminService {
    private final AdminUserRepository admins;
    private final CoachingAccountRepository coachings;
    private final PasswordEncoder encoder;
    private final JwtTokenService tokens;
    private final RestClient restClient;
    @Value("${app.student-service-url:http://localhost:8082}") private String studentServiceUrl;

    public AdminLoginResponse login(AdminLoginRequest request) {
        AdminUser admin = admins.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!encoder.matches(request.getPassword(), admin.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        return new AdminLoginResponse("Login successful", tokens.issue(admin), admin.getId(), "ADMIN");
    }

    public List<CoachingAdminView> pendingCoachings() {
        return coachings.findAll().stream().filter(c -> !c.isVerified()).map(CoachingAdminView::from).toList();
    }

    public List<CoachingAdminView> coachings() {
        return coachings.findAll().stream().map(CoachingAdminView::from).toList();
    }

    @Transactional
    public CoachingAdminView approve(Long id) {
        CoachingAccount coaching = coachings.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coaching request not found"));
        coaching.setVerified(true);
        return CoachingAdminView.from(coaching);
    }

    @Transactional
    public void deleteCoaching(Long id) {
        if (!coachings.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coaching not found");
        coachings.deleteById(id);
    }

    public List<StudentAdminView> students(String authorization) {
        StudentAdminView[] rows = restClient.get().uri(studentServiceUrl + "/student/admin/students")
                .header("Authorization", authorization).retrieve().body(StudentAdminView[].class);
        return rows == null ? List.of() : List.of(rows);
    }

    public StudentAdminView student(Long id, String authorization) {
        return restClient.get().uri(studentServiceUrl + "/student/admin/students/{id}", id)
                .header("Authorization", authorization).retrieve().body(StudentAdminView.class);
    }
}
