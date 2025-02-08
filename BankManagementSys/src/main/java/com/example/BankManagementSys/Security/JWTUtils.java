package com.example.BankManagementSys.Security;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTUtils {
    @Value("${application.security.jwt.security-key}")
    private String jwtSecret;

    @Value("${application.security.jwt.expiration}")
    private int expirationMinutes;

    private Key secretKey;


    @PostConstruct
    private void init() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret); // Decode the Base64 key
        this.secretKey = Keys.hmacShaKeyFor(keyBytes); // Ensure it's 512-bit
    }

    public String generateJWTToken(String username) {
        try {
            System.out.println("JWTUtils: Generating token for username: " + username);
            Date expirationTime = new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000);

            String token = Jwts.builder()
                    .setIssuer("R-Labs-Adan&YaraApp")
                    .setExpiration(expirationTime)
                    .setIssuedAt(new Date())
                    .setSubject(username)
                    .signWith(secretKey, SignatureAlgorithm.HS512) // Secure signing
                    .compact();

            System.out.println("JWTUtils: Token successfully generated: " + token);
            return token;
        } catch (Exception e) {
            System.err.println("JWTUtils: Error while generating token: " + e.getMessage());
            e.printStackTrace();
            throw e; // Rethrow the exception to be caught in the controller
        }
    }
    public Claims verifyToken(String token)
            throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException {
        // Parse the JWT token and extract claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(this.secretKey) // Set the signing key
                .build()
                .parseClaimsJws(token) // Parse the JWT and validate
                .getBody(); // Extract claims from the token

        return claims; // Return the claims object
    }


}
