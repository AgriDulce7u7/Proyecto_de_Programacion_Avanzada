package com.uq.triage.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * Utilidad para generar y validar tokens JWT.
 * La clave secreta se configura en application.properties (jwt.secret).
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    public String generarToken(String correo, String rol, Long id) {
        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .claim("id", id)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    public String extraerCorreo(String token) { return getClaims(token).getSubject(); }
    public String extraerRol(String token)    { return getClaims(token).get("rol", String.class); }
    public Long   extraerId(String token)     { return getClaims(token).get("id", Long.class); }

    public boolean esValido(String token) {
        try { getClaims(token); return true; }
        catch (JwtException | IllegalArgumentException e) { return false; }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
