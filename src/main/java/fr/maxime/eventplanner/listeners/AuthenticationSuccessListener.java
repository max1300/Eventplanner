package fr.maxime.eventplanner.listeners;

import fr.maxime.eventplanner.models.AppUser;
import fr.maxime.eventplanner.services.login.LoginAttemptService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthenticationSuccessListener {

    private final LoginAttemptService loginAttemptService;

    @EventListener
    public void onAuthenticationSucess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof AppUser) {
            AppUser user = (AppUser) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromCache(user.getUsername());
        }
    }
}
