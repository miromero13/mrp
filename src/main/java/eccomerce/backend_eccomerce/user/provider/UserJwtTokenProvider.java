package eccomerce.backend_eccomerce.user.provider;

import eccomerce.backend_eccomerce.user.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UserJwtTokenProvider {

    // Generates a random secure key for HS512. 
    // Note: This resets on every application restart.
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Value("${jwt.expiration}")
    private long jwtExpirationInMillis;

    /**
     * Generate a JWT token
     */
    public String generateToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(user.email)
                .claim("email", user.email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMillis))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Validate the JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder() // Updated to use parserBuilder()
                .setSigningKey(secretKey)
                .build() // New way to build the parser
                .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Get user ID from the token
     */
    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder() // Updated to use parserBuilder()
                .setSigningKey(secretKey)
                .build() // New way to build the parser
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Get email from the token
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}