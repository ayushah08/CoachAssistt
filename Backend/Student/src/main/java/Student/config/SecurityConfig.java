package Student.config;

import Student.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity // <--- Prevents DB execution if authorization fails
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/student/login", "/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/student/register").hasRole("COACHING") // <--- Fixed path
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
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://127.0.0.1:5501",
                "http://localhost:5501",
                "http://localhost:64422",
                "http://localhost:3000"
        ));        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
