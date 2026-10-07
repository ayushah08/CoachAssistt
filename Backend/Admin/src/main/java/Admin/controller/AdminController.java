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
    public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) { 
        return service.login(request); }
    @GetMapping("/coaching/requests")
    public ResponseEntity<List<CoachingAdminView>> requests() { return service.pendingCoachings(); }
    @GetMapping("/coachings")
    public ResponseEntity<List<CoachingAdminView>> coachings() { return (ResponseEntity<List<CoachingAdminView>>) service.coachings(); }
    @PostMapping("/coaching/{id}/approve")
    public CoachingAdminView approve(@PathVariable Long id) { return service.approve(id); }
    @DeleteMapping("/coaching/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {  return  service.deleteCoaching(id); }
    @GetMapping("/students")
    public ResponseEntity<List<StudentAdminView>> students(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) { return service.students(auth); }
    @GetMapping("/students/{id}")
    public ResponseEntity<StudentAdminView> student(@PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String auth) { return service.student(id, auth); }
}
