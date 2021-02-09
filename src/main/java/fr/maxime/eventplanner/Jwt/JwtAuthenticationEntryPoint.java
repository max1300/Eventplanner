package fr.maxime.eventplanner.Jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.maxime.eventplanner.dtos.CustomHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Locale;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {
    // Custom class pour surchargé le comportement d'une erreur lors d'une authentification insuffisante

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2) throws IOException {
        CustomHttpResponse httpResponse = new CustomHttpResponse(
                LocalDate.now(),
                FORBIDDEN.value(),
                FORBIDDEN,
                FORBIDDEN.getReasonPhrase().toUpperCase(Locale.ROOT),
                "Vous devez être loggé pour accéder à ce contenu"
        );

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        OutputStream stream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(stream, httpResponse);
        stream.flush();
    }
}
