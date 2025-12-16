package com.apexauth.apexauth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

  private final Key key;
  private final long expirationMs;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.expiration-ms}") long expirationMs) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
    this.expirationMs = expirationMs;
  }

  public String generateToken(Long userId, String email, String role) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationMs);

    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .claim("email", email)
        .claim("role", role)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractSubject(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public String extractEmail(String token) {
    return extractClaim(token, claims -> claims.get("email", String.class));
  }

  public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
    return resolver.apply(claims);
  }

  public boolean isTokenValid(String token) {
    try {
      Date expiration = extractClaim(token, Claims::getExpiration);
      return expiration.after(new Date());
    } catch (Exception ex) {
      return false;
    }
  }
}
