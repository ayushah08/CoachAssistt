package Admin.security;

import Admin.entity.AdminUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtTokenService {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expiration-ms:3600000}") private long expirationMs;

    private SecretKey key() { return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)); }

    public String issue(AdminUser admin) {
        Date now = new Date();
        return Jwts.builder().subject(admin.getEmail()).claim("userId", admin.getId()).claim("role", "ADMIN")
                .issuedAt(now).expiration(new Date(now.getTime() + expirationMs)).signWith(key()).compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
    }
}
