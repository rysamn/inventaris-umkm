package com.umkm.inventaris.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // Secret key untuk signing JWT (sebaiknya simpan di environment variable di production)
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    // Token valid selama 24 jam
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    public String generateToken(String namaPengguna, String peran, Integer id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("peran", peran);
        claims.put("id", id);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(namaPengguna)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) extractClaims(token).get("peran");
    }

    public Integer extractId(String token) {
        return (Integer) extractClaims(token).get("id");
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String namaPengguna) {
        final String username = extractUsername(token);
        return (username.equals(namaPengguna) && !isTokenExpired(token));
    }
}