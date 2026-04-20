package eccomerce.backend_eccomerce.user.provider;

import eccomerce.backend_eccomerce.user.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class UserJwtTokenProvider {

    private SecretKey secretKey;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMillis;

    @PostConstruct
    public void init() {
        try {
            // Derive a deterministic 64-byte key suitable for HS512 from configured secret text.
            byte[] keyBytes = MessageDigest.getInstance("SHA-512")
                    .digest(jwtSecret.getBytes(StandardCharsets.UTF_8));
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("No se pudo inicializar la clave JWT", exception);
        }
    }

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