package it.schipani.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.schipani.businessLayer.security.SecurityUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.key}")
    private String securityKey;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = securityKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication auth) {
        SecurityUserDetails user = (SecurityUserDetails) auth.getPrincipal();
        SecretKey key = getSigningKey();

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .issuer("MySpringApplication")
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                //ESISTONO I CLAIM OVVERO SONO INFORMAZIONI AGGIUNTIVE CHE POSOSNO ESSERE AGGIUNTE AL TOKEN .claim("professore dell'aula", "Mauro")
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {

            SecretKey key = getSigningKey();

            Jwts.parser().verifyWith(key)
                    .requireIssuer("MySpringApplication").build().
                    parseSignedClaims(token);

            Date expirationDate = getExpirationDateFromToken(token);
            if (expirationDate.before(new Date()))
                throw new JwtException("Token expired");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        SecretKey key = getSigningKey();
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private Date getExpirationDateFromToken(String token) {
        SecretKey key = getSigningKey();
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getExpiration();
    }

}
