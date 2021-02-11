package fr.maxime.eventplanner.services;

import fr.maxime.eventplanner.models.AppUser;
import fr.maxime.eventplanner.models.AppUserPrincipal;
import fr.maxime.eventplanner.repositories.AppUserRepository;
import fr.maxime.eventplanner.services.login.LoginAttemptService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserRepository repository;
    private final LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = repository.findAppUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format("Username with username %s not found", username)));

        try {
            validatingLoginAttempt(user);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return new AppUserPrincipal(user);
    }

    private void validatingLoginAttempt(AppUser user) throws ExecutionException {
        if (user.isNotLocked()) {
            if (loginAttemptService.hasExcedeedNumberOfAttempts(user.getUsername())) {
                user.setActive(false);
            } else {
                user.setActive(true);
            }

        } else {
            loginAttemptService.evictUserFromCache(user.getUsername());
        }
    }
}
