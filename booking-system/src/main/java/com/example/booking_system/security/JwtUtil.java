package com.example.booking_system.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key = Keys.secretKeyFor( SignatureAlgorithm.HS256 );
    private final long EXPIRATION_MS = 3600000;

    public String generateToken( String email , String role ) {
         return Jwts.builder()
            .subject(email)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
            .signWith(key)
            .compact();
    }

    public String extractEmail( String token ) {
        return Jwts.parser().verifyWith( key ).build().parseSignedClaims( token ).getPayload().getSubject();

    }

    public boolean isTokenValid( String token ) {

        try {
            String email = extractEmail(token);
            return email != null; // Returns true if extraction succeeds and returns a value
        } catch (Exception e) {
            return false;
        }
    }

    public String extractRole( String token ) {

        return Jwts.parser().verifyWith( key ).build().parseSignedClaims( token ).getPayload().get( "role" , String.class);

    }

}