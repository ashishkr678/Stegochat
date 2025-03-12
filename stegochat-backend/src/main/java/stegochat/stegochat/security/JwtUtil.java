package stegochat.stegochat.security;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import stegochat.stegochat.entity.UsersEntity;
import stegochat.stegochat.repository.UserRepository;

@Component
@Slf4j
public class JwtUtil {

    private final SecretKey SECRET_KEY;
    private final UserRepository userRepository;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    public JwtUtil(@Value("${jwt.secret}") String secretKey, UserRepository userRepository) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("JWT secret key is missing. Set it in the environment variables.");
        }
        this.SECRET_KEY = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.userRepository = userRepository;
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SECRET_KEY)
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token has expired: {}", e.getMessage());
            markUserOffline(e.getClaims().getSubject());
            throw new JwtException("Token has expired", e);
        } catch (JwtException e) {
            log.warn("Invalid token: {}", e.getMessage());
            throw new JwtException("Invalid token", e);
        }
    }

    public void markUserOffline(String username) {
        UsersEntity user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setOnline(false);
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);
        }
    }
}
