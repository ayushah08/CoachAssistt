package Coach_Service.config;


import Coach_Service.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter authenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/coaching/register", "/api/v1/coaching/login",
                                "api/v1/coaching/health",
                                "/api/v1/student/login", "/api/v1/parents/login",
                                "/actuator/health").permitAll()
                        .requestMatchers("/api/v1/parents/register").hasRole("COACHING")
                        .requestMatchers("/api/v1/parents/students/**").hasRole("COACHING")
                        .requestMatchers("/api/v1/student/create").hasRole("COACHING")
                        .requestMatchers("/api/v1/student/**").hasRole("COACHING")
                        .requestMatchers(HttpMethod.POST, "/api/v1/attendance/students/**").hasRole("COACHING")
                        .requestMatchers(HttpMethod.GET, "/api/v1/attendance/me")
                                .hasAnyRole("STUDENT", "PARENT")
                        .requestMatchers("/api/v1/attendance/**").denyAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/marks/me").hasAnyRole("STUDENT", "PARENT")
                        .requestMatchers("/api/v1/marks/students/**").hasRole("COACHING")
                        .requestMatchers("/api/v1/marks/**").denyAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/notices/me").hasAnyRole("STUDENT", "PARENT")
                        .requestMatchers("/api/v1/notices", "/api/v1/notices/**").hasRole("COACHING")
                        .requestMatchers("/api/v1/notices/**").denyAll()
                        .anyRequest().authenticated())
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5501")
        );

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH","OPTIONS"));

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setExposedHeaders(List.of("Authorization"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


}
