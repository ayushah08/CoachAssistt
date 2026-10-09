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
@EnableMethodSecurity
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
                        // Permit login endpoints matching Postman URIs
                        .requestMatchers("/api/v1/student/login", "/student/login", "/actuator/health").permitAll()

                        // Permit create/register endpoints for COACHING
                        .requestMatchers(HttpMethod.POST, "/api/v1/student/create", "/api/v1/student/register", "/student/register").hasRole("COACHING")
                        .requestMatchers("/api/v1/student/admin/students/**", "/student/admin/students/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/student/**", "/student/**").hasRole("COACHING")

                        .requestMatchers("/api/v1/marks/me", "/marks/me").hasAnyRole("STUDENT", "PARENT")
                        .requestMatchers("/api/v1/marks/students/**", "/marks/students/**").hasRole("COACHING")
                        .requestMatchers("/api/v1/notices/me", "/notices/me").hasAnyRole("STUDENT", "PARENT")
                        .requestMatchers("/api/v1/notices/**", "/notices/**").hasRole("COACHING")
                        .requestMatchers(HttpMethod.POST, "/api/v1/attendance/students/**", "/attendance/students/**").hasRole("COACHING")
                        .requestMatchers(HttpMethod.GET, "/api/v1/attendance/me", "/attendance/me").hasAnyRole("STUDENT", "PARENT")
                        .requestMatchers("/api/v1/attendance/**", "/attendance/**").denyAll()
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
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}