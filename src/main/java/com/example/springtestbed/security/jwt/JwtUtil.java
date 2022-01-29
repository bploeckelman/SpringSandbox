package com.example.springtestbed.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtil {

    /**
     * References:
     * https://github.com/jwtk/jjwt
     * https://www.baeldung.com/java-json-web-tokens-jjwt
     * https://www.viralpatel.net/java-create-validate-jwt-token/
     * jwt auth flow: https://www.javainuse.com/spring/boot-jwt
     */

    private static final long JWT_LIFETIME_HOURS   = 24;

    private final Key key;

    JwtUtil() {
        // TODO - read the key from the application config
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateToken(UserDetails userDetails) {
        var claims = new HashMap<String, Object>();
        // TODO - add custom claims
        //  claims.add("name", ...)
        //  claims.add("email", ...)
        //  etc...
        var subject = userDetails.getUsername();
        var jti = UUID.randomUUID().toString() ;
        var now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setId(jti)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(JWT_LIFETIME_HOURS, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public boolean canTokenBeRefreshed(String token) {
        return (isNotExpired(token) || isExpirationIgnored(token));
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        var username = getUsernameFromToken(token);
        var isSameUser = username.equals(userDetails.getUsername());
        return (isSameUser && isNotExpired(token));
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        var claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        var parser = Jwts.parserBuilder().setSigningKey(key).build();
        try {
            var claims = parser.parseClaimsJws(token);
            return claims.getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            // TODO - handle exceptions differently instead of rethrowing?
            throw new JwtException("Cannot extract claims from token", e);
        }
    }

    private boolean isNotExpired(String token) {
        var expiration = getExpirationDateFromToken(token);
        return expiration.after(Date.from(Instant.now()));
    }

    private boolean isExpirationIgnored(String token) {
        // TODO - specify tokens where expiration is ignored
        return false;
    }

}
