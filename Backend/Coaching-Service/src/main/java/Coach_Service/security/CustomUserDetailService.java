package Coach_Service.security;

import Coach_Service.repository.CoachingRespoitory;
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
        return coachingRepository.findByCoachingEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Coaching profile not found with email: " + username));
    }
}
