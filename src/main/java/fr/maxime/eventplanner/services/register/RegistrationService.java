package fr.maxime.eventplanner.services.register;

import fr.maxime.eventplanner.dtos.RegistrationRequest;
import fr.maxime.eventplanner.exceptions.*;
import fr.maxime.eventplanner.models.AppUser;
import fr.maxime.eventplanner.models.AppUserRole;
import fr.maxime.eventplanner.models.ConfirmationToken;
import fr.maxime.eventplanner.services.AppUserService;
import fr.maxime.eventplanner.services.ConfirmationTokenService;
import fr.maxime.eventplanner.validators.EmailValidator;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    public static final Logger LOG = LoggerFactory.getLogger(RegistrationService.class);
    public static final String EMAIL_ALREADY_TAKEN = "Email déjà pris !!";
    public static final String EMAIL_NOT_VALID = "Email non valide !!";
    public static final String TOKEN_ALREADY_CONFIRMED = "Le token de confirmation est déjà confirmé.";
    public static final String TOKEN_EXPIRED = "Le token de confirmation est expiré.";

    private final AppUserService service;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailValidator emailValidator;

    @Transactional
    public ResponseEntity<AppUser> registerUser(RegistrationRequest request) throws EmailNotValidException, EmailAlreadyExistException, UsernameAlreadyExistException {
        if (!emailValidator.test(request.getEmail())) {
            throw new EmailNotValidException(EMAIL_NOT_VALID);
        }

        AppUser checkEmailAlreadyTaken = service.getByEmail(request.getEmail());
        if (checkEmailAlreadyTaken != null) {
            throw new EmailAlreadyExistException(EMAIL_ALREADY_TAKEN);
        }

        AppUser user = new AppUser(request.getUsername(), request.getEmail(), request.getPassword(), AppUserRole.USER);
        return service.create(user);
    }

    @Transactional
    public ResponseEntity<AppUser> enableAccount(String token) throws TokenAlreadyConfirmedException, TokenExpiredException, AppUserNotFoundException {
        ConfirmationToken byToken = confirmationTokenService.getByToken(token);
        AppUser user = service.getById(byToken.getAppUser().getAppUserId());

        if (byToken.getConfirmedAt() != null) {
            throw new TokenAlreadyConfirmedException(TOKEN_ALREADY_CONFIRMED);
        }
        boolean checkExpirationToken = confirmationTokenService.checkExpirationToken(byToken);
        if (checkExpirationToken) {
            throw new TokenExpiredException(TOKEN_EXPIRED);
        }

        byToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenService.update(byToken.getConfirmationTokenId(), byToken);
        user.setEnabled(true);
        service.update(user.getAppUserId(), user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


}
