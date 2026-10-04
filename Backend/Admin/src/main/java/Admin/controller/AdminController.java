package Admin.controller;

import Admin.dto.AdminLoginRequest;
import Admin.dto.AdminLoginResponse;
import Admin.dto.CoachingAdminView;
import Admin.dto.StudentAdminView;
import Admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/v1/admin") @RequiredArgsConstructor
public class AdminController {
    private final AdminService service;

    @PostMapping("/login")
    public AdminLoginResponse login(@Valid @RequestBody AdminLoginRequest request) { return service.login(request); }
    @GetMapping("/coaching/requests")
    public List<CoachingAdminView> requests() { return service.pendingCoachings(); }
    @GetMapping("/coachings")
    public List<CoachingAdminView> coachings() { return service.coachings(); }
    @PostMapping("/coaching/{id}/approve")
    public CoachingAdminView approve(@PathVariable Long id) { return service.approve(id); }
    @DeleteMapping("/coaching/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.deleteCoaching(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/students")
    public List<StudentAdminView> students(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) { return service.students(auth); }
    @GetMapping("/students/{id}")
    public StudentAdminView student(@PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String auth) { return service.student(id, auth); }
}
