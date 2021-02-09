package fr.maxime.eventplanner.controllers;

import fr.maxime.eventplanner.dtos.AuthenticationResponse;
import fr.maxime.eventplanner.dtos.JwtUsernamePasswordRequest;
import fr.maxime.eventplanner.dtos.RegistrationRequest;
import fr.maxime.eventplanner.models.AppUser;
import fr.maxime.eventplanner.services.LoginService;
import fr.maxime.eventplanner.services.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
@CrossOrigin
@AllArgsConstructor
public class AuthenticationController {

    private final RegistrationService service;
    private final LoginService loginService;

    @PostMapping("/register")
    public ResponseEntity<AppUser> registerAppUser(@RequestBody RegistrationRequest request) {
        return service.registerUser(request);
    }

    @GetMapping("/confirm")
    public ResponseEntity<AppUser> confirmToken(@RequestParam("token")String token) {
        return service.enableAccount(token);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> loginAppUser(@RequestBody JwtUsernamePasswordRequest request) {
        return loginService.authenticationAttempt(request);
    }


}
