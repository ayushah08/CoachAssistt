package Coach_Service.service;

import Coach_Service.dto.Login.CoachingLoginRequest;
import Coach_Service.dto.Login.LoginResponse;
import Coach_Service.dto.Register.CoachingRegisterRequest;
import Coach_Service.dto.Register.CoachingRegisterResponse;
import Coach_Service.entity.Coaching;
import Coach_Service.entity.Role;
import Coach_Service.repository.CoachingRespoitory;
import Coach_Service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CoachingService {
    private final CoachingRespoitory coachingRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public CoachingRegisterResponse register(CoachingRegisterRequest request) {
        if (coachingRepository.existsByCoachingEmailIgnoreCase(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account already uses this email");
        }
        if (coachingRepository.findByCoachingName(request.getCoachingName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This coaching name is already registered");
        }

        Coaching coaching = coachingRepository.save(Coaching.builder()
                .coachingName(request.getCoachingName().trim())
                .coachingAddress(request.getCoachingAddress().trim())
                .coachingEmail(request.getEmail().trim().toLowerCase())
                .coachingOwnerName(request.getCoachingOwnerName().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .dateTime(LocalDateTime.now())
                .role(Role.COACHING)
                .verified(false)
                .build());

        return CoachingRegisterResponse.builder()
                .message("Registration request submitted. Coaching access is pending admin approval.")
                .coachingId(coaching.getCoachingId())
                .coachingName(coaching.getCoachingName())
                .email(coaching.getCoachingEmail())
                .ownerName(coaching.getCoachingOwnerName())
                .build();
    }

    public LoginResponse login(CoachingLoginRequest request) {
        Coaching coaching = coachingRepository.findByCoachingEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), coaching.getPassword()) || !coaching.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return new LoginResponse("Login successful", jwtService.generateToken(coaching),
                coaching.getCoachingId(), Role.COACHING.name());
    }

    public String coachingNameForEmail(String email) {
        return coachingRepository.findByCoachingEmailIgnoreCase(email)
                .map(Coaching::getCoachingName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid account"));
    }
}
