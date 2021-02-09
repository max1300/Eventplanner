package fr.maxime.eventplanner.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class JwtProvider {

    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;

    public String createToken(Authentication authentication) {
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("authorities", authentication.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusWeeks(2)))
                .signWith(secretKey)
                .compact();
    }

    public Jws<Claims> jwsVerifier(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);
    }

    public List<Map<String, String>> getJwtAuthorities(Claims body) {
        return (List<Map<String, String>>) body.get("authorities");
    }

    public String getJwtSubject(Claims body) {
        return body.getSubject();
    }

    public Claims getJwtBody(Jws<Claims> claimsJws) {
        return claimsJws.getBody();
    }

    public String getJwtTokenPrefix() {
        return jwtConfig.getTokenPrefix();
    }

    public String getAuthorizationHeader() {
        return jwtConfig.getAuthorizationHeader();
    }
}
