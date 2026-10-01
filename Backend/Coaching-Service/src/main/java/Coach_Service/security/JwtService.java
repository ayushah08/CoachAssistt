package Coach_Service.security;

import Coach_Service.entity.Coaching;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;


import java.security.Key;

@Service
public class JwtService {

    @Value("${jwt.secret")
    private String secret;

    @Value("${jwt.expiration")
    private Long expiration;


    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    // generating tokens
    private String generatedJwtToken(UserDetails userDetails){

        Coaching user = (Coaching) userDetails;

        return Jwts.builder()

                .subject(
                        user.getUsername()
                )

                .claim(
                        "userId",
                        user.getUserId().toString()
                )

                .claim(
                        "role",
                        user.getRole().name()
                )

                .issuedAt(
                        new Date()
                )

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )

                .signWith(
                        getSignInKey()
                )

                .compact();


    }

    public String extractUsername(
    String token){
        return extractClaim(token ,  Claims :: getSubject);
    }


    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        Claims claims =
                extractAllClaims(token);

        return claimsResolver.apply(
                claims
        );
    }

    public String extractUserId(
            String token
    ) {

        return extractClaim(
                token,
                claims ->
                        claims.get(
                                "userId",
                                String.class
                        )
        );
    }


    // ==========================
    // Extract Role
    // ==========================

    public String extractRole(
            String token
    ) {

        return extractClaim(
                token,
                claims ->
                        claims.get(
                                "role",
                                String.class
                        )
        );
    }


    // ==========================
    // Extract All Claims
    // ==========================

    private Claims extractAllClaims(
            String token
    ) {

        return Jwts.parser()

                .verifyWith(
                        (SecretKey) getSignInKey()
                )

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }


    // ==========================
    // Expiration Check
    // ==========================

    private boolean isTokenExpired(
            String token
    ) {

        return extractExpiration(token)
                .before(new Date());
    }


    public Date extractExpiration(
            String token
    ) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }

    // ==========================
    // Validate Token
    // ==========================

    public boolean validateJwtToken(
            String token,
            UserDetails userDetails
    ) {

        try {

            String username =
                    extractUsername(token);

            return username.equals(
                    userDetails.getUsername()
            )
                    && !isTokenExpired(token);

        } catch (
                ExpiredJwtException |
                MalformedJwtException |
                UnsupportedJwtException |
                SignatureException |
                IllegalArgumentException e
        ) {

            return false;
        }
    }

}
