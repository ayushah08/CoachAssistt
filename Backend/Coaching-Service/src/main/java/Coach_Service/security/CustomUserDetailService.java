package coach_service.security;

import Coach_Service.repository.CoachingRespoitory;
import coach_service.repository.CoachingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final CoachingRespoitory coachingRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return coachingRepository.findByCoachingName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Coaching profile not found with name: " + username));
    }
}