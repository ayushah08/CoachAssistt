package Coach_Service.security;

import Coach_Service.entity.Coaching;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(Coaching user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getUserId())
                .claim("role", user.getRole().name())
                .claim("coachingName", user.getCoachingName())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(signingKey())
                .compact();
    }

    public String generateAdminToken(String email, Long adminId) {
        Date now = new Date();
        return Jwts.builder().subject(email).claim("userId", adminId).claim("role", "ADMIN")
                .issuedAt(now).expiration(new Date(now.getTime() + expirationMs))
                .signWith(signingKey()).compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(signingKey()).build()
                .parseSignedClaims(token).getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(Jwts.parser().verifyWith(signingKey()).build()
                .parseSignedClaims(token).getPayload());
    }

    public boolean validate(String token, UserDetails user) {
        try {
            Claims claims = Jwts.parser().verifyWith(signingKey()).build()
                    .parseSignedClaims(token).getPayload();
            return user.getUsername().equals(claims.getSubject())
                    && claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());
        } catch (RuntimeException exception) {
            return false;
        }
    }
}
