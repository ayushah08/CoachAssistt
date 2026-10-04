package Parent.config;

import Parent.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/student/login", "/actuator/health").permitAll()
                        .requestMatchers("/student/admin/students/**").hasRole("ADMIN")
                        .requestMatchers("/student/**").hasRole("COACHING")
                        .requestMatchers("/marks/me").hasAnyRole("STUDENT", "PARENT")
                        .requestMatchers("/marks/students/**").hasRole("COACHING")
                        .requestMatchers("/notices/me").hasAnyRole("STUDENT", "PARENT")
                        .requestMatchers("/notices", "/notices/**").hasRole("COACHING")
                        .requestMatchers(HttpMethod.POST, "/attendance/students/**").hasRole("COACHING")
                        .requestMatchers(HttpMethod.GET, "/attendance/me").hasAnyRole("STUDENT", "PARENT")
                        .requestMatchers("/attendance/**").denyAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
