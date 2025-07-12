package org.zarhub.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zarhub.model.Users;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil implements Serializable {
    private static String secretKey;
    private static int expirationMinutes;
    private static final String claims_key = "userData";

    @Value("${jwt.secretKey}")
    public void setSecret(String secretKey) {
        this.secretKey = secretKey;
    }

    @Value("${jwt.expirationMinutes}")
    public void setExpirationMinutes(int expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }

    public static String createToken(Object user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(claims_key, user);
        return generateToken(claims);
    }

    public static Object getTokenData(String token) {
        Claims claims = extractAllClaims(token);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(claims.get(claims_key), Users.class);
    }

    private static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(DateUtils.addMinutes(new Date(), expirationMinutes))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private static String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private static <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.resolve(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    private static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    public static boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    @FunctionalInterface
    private static interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }

}
