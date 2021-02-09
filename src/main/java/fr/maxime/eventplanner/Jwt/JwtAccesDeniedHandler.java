package fr.maxime.eventplanner.Jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.maxime.eventplanner.dtos.CustomHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Locale;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class JwtAccesDeniedHandler implements AccessDeniedHandler {
    // Custom class pour surchargé le comportement d'une erreur lors d'un manque de permission

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
            throws IOException, ServletException {
        CustomHttpResponse httpResponse = new CustomHttpResponse(
                LocalDate.now(),
                UNAUTHORIZED.value(),
                UNAUTHORIZED,
                UNAUTHORIZED.getReasonPhrase().toUpperCase(Locale.ROOT),
                "Vous n'avez pas les permissions suffisantes pour accéder à ce contenu"
        );

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(UNAUTHORIZED.value());
        OutputStream stream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(stream, httpResponse);
        stream.flush();

    }
}
