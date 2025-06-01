package com.mvc.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;

public class JwtUtil {

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("claveSuperSecretaYSeguraParaJWT123456".getBytes());
    private static final long EXPIRATION_TIME = 86400000; // 24 horas

    public static String generarToken(Long idUsuario, int rolId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rolId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(idUsuario))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Jws<Claims> validarToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token);
    }
}

