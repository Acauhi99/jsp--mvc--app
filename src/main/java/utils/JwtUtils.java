package utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class JwtUtils {
    private static final String SECRET_KEY = "your_jwt_secret_key_should_be_at_least_256_bits_long_for_security";
    private static final long JWT_EXPIRATION = 1000 * 60 * 60 * 24; 
    
    private static Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
    
    public static String generateToken(UUID userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, userId.toString(), email);
    }
    
    private static String createToken(Map<String, Object> claims, String subject, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .claim("email", email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    public static String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }
    
    public static String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    
    public static UUID extractUserId(String token) {
        String subject = extractClaim(token, Claims::getSubject);
        return UUID.fromString(subject);
    }
    
    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}