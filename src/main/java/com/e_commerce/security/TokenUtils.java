package com.e_commerce.security;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.e_commerce.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;

public class TokenUtils {

    // Semilla secreta (debe ser larga y segura)
    private final static String ACCESS_TOKEN_SECRET = "transferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJacatransferJaca";

    // Tiempo de vida del token (4 minutos)
    private final static Long ACCESS_TOKEN_LIFE_TIME = 60 * 60 * 1000L; 

    // Genera JWT puro (sin "Bearer ")
    public static String generateToken(User user) {
        Date expirationDate = new Date(System.currentTimeMillis() + ACCESS_TOKEN_LIFE_TIME);
        Map<String, Object> payload = new HashMap<>();

        payload.put("id", user.getId());
        payload.put("username", user.getUsername());
        payload.put("name", user.getName());
        payload.put("email", user.getEmail());
        payload.put("gender", user.getGender());
        payload.put("address", user.getAddress());
        payload.put("status", user.getStatus());
        payload.put("role", user.getRole());
        payload.put("registrationDate", user.getRegistrationDate().toString());

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(expirationDate)
                .claims(payload)
                .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
                .compact();
    }

    public static UsernamePasswordAuthenticationToken decodeToken(String token) {

        if (!token.startsWith("Bearer ")) {
            throw new MalformedJwtException("Formato no válido, falta 'Bearer '");
        }

        token = token.substring(7); 

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        if (role == null) {
            throw new MalformedJwtException("El token no contiene rol");
        }

        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}
