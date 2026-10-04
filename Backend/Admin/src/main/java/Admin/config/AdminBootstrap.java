package Admin.config;

import Admin.entity.AdminUser;
import Admin.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {
    private final AdminUserRepository admins;
    private final PasswordEncoder encoder;
    @Value("${admin.email:}") private String email;
    @Value("${admin.password:}") private String password;

    @Override public void run(String... args) {
        if (email.isBlank() && password.isBlank()) return;
        if (email.isBlank() || password.isBlank())
            throw new IllegalStateException("Set both ADMIN_EMAIL and ADMIN_PASSWORD to bootstrap the admin account");
        if (!admins.existsByEmailIgnoreCase(email))
            admins.save(AdminUser.builder().email(email.trim().toLowerCase()).password(encoder.encode(password)).build());
    }
}
