package fr.maxime.eventplanner.services;

import fr.maxime.eventplanner.Jwt.JwtProvider;
import fr.maxime.eventplanner.dtos.AuthenticationResponse;
import fr.maxime.eventplanner.dtos.JwtUsernamePasswordRequest;
import fr.maxime.eventplanner.models.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public ResponseEntity<AuthenticationResponse> authenticationAttempt(JwtUsernamePasswordRequest request) {

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String jwtProviderToken = jwtProvider.createToken(authenticate);

        AuthenticationResponse response = getAuthenticationResponse(authenticate, jwtProviderToken);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    private AuthenticationResponse getAuthenticationResponse(Authentication authentication, String jwtToken) {
        AppUser principal = (AppUser) authentication.getPrincipal();
        return new AuthenticationResponse(jwtToken, principal.getUsername(), principal.getAppUserRole().name());
    }
}
