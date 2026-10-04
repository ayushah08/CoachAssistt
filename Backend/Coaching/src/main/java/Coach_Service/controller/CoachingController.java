package Coach_Service.controller;


import Coach_Service.dto.Login.CoachingLoginRequest;
import Coach_Service.dto.Login.LoginResponse;
import Coach_Service.dto.Register.CoachingRegisterRequest;
import Coach_Service.dto.Register.CoachingRegisterResponse;
import Coach_Service.service.CoachingService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coaching")
@RequiredArgsConstructor
public class CoachingController {

    private final CoachingService coachingService;

    @PostMapping("/register")
    public ResponseEntity<CoachingRegisterResponse> register(@Valid @RequestBody CoachingRegisterRequest registerRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(coachingService.register(registerRequest));

    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody CoachingLoginRequest loginRequest){

        return coachingService.login(loginRequest);
    }
}
