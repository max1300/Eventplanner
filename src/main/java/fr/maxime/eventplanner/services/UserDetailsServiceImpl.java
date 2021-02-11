package fr.maxime.eventplanner.services;

import fr.maxime.eventplanner.models.AppUser;
import fr.maxime.eventplanner.models.AppUserPrincipal;
import fr.maxime.eventplanner.repositories.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = repository.findAppUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format("Username with username %s not found", username)));

        return new AppUserPrincipal(user);
    }
}
