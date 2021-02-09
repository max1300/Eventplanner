package fr.maxime.eventplanner.Jwt;

import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.var;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

@AllArgsConstructor
@Component
public class JwtVerifierFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase("options")){
            response.setStatus(OK.value());

        } else {
            // On recupere le token dans le header authorization de la requete
            String authorizationHeader = request.getHeader(jwtProvider.getAuthorizationHeader());

            // Si le String du token est vide ou si il ne contient pas le prefix Bearer on laisse la requete continuer dans le vide
            if (Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith(jwtProvider.getJwtTokenPrefix())) {
                filterChain.doFilter(request, response);
                return;
            }

            // On enleve le prefix pour traiter juste le token
            String token = authorizationHeader.replace(jwtProvider.getJwtTokenPrefix(), "");

            try {
                // JJWT verifie la validité du token en le reconstruisant comme un Jwt signé (JWS) avec le parser
                Jws<Claims> claimsJws = jwtProvider.jwsVerifier(token);

                // On récupère le corps du jws
                Claims body = jwtProvider.getJwtBody(claimsJws);

                // Le sujet qui doit etre le username
                String username = jwtProvider.getJwtSubject(body);

                // La liste des authorities (donc le(s) role)
                List<Map<String, String>> authorities = jwtProvider.getJwtAuthorities(body);

                // On reconstruit cette liste des authorities
                Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                        .map(m -> new SimpleGrantedAuthority(m.get("authority")))
                        .collect(Collectors.toSet());

                // On procède à une authentification du user
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, simpleGrantedAuthorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JwtException e) {
                SecurityContextHolder.clearContext();
                throw new IllegalStateException(String.format("Token %s cannot be trusted", token));
            }

            filterChain.doFilter(request, response);
        }
    }

}
