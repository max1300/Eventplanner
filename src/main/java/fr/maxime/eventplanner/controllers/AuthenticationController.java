package fr.maxime.eventplanner.controllers;

import fr.maxime.eventplanner.dtos.AuthenticationResponse;
import fr.maxime.eventplanner.dtos.JwtUsernamePasswordRequest;
import fr.maxime.eventplanner.dtos.RegistrationRequest;
import fr.maxime.eventplanner.exceptions.*;
import fr.maxime.eventplanner.models.AppUser;
import fr.maxime.eventplanner.services.login.LoginService;
import fr.maxime.eventplanner.services.register.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600, methods = {RequestMethod.GET, RequestMethod.POST})
@RestController
@RequestMapping("/authentication")
@AllArgsConstructor
public class AuthenticationController {

    private final RegistrationService service;
    private final LoginService loginService;

    @PostMapping("/register")
    public AppUser registerAppUser(@RequestBody RegistrationRequest request) throws EmailNotValidException, EmailAlreadyExistException, UsernameAlreadyExistException {
        AppUser user = service.registerUser(request);
        return user;

    }

    @GetMapping("/confirm")
    public ResponseEntity<AppUser> confirmToken(@RequestParam("token")String token) throws TokenExpiredException, TokenAlreadyConfirmedException, AppUserNotFoundException {
        return service.enableAccount(token);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> loginAppUser(@RequestBody JwtUsernamePasswordRequest request) {
        return loginService.authenticationAttempt(request);
    }


}
