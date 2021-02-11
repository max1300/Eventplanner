package fr.maxime.eventplanner.services;

import fr.maxime.eventplanner.exceptions.AppUserNotFoundException;
import fr.maxime.eventplanner.exceptions.UsernameAlreadyExistException;
import fr.maxime.eventplanner.models.AppUser;
import fr.maxime.eventplanner.repositories.AppUserRepository;
import fr.maxime.eventplanner.services.login.LoginAttemptService;
import lombok.AllArgsConstructor;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
@Transactional
public class AppUserService implements UserDetailsService {

    public static final Logger LOG = LoggerFactory.getLogger(AppUserService.class);
    public static final String USER_NOT_FOUND = "User not found with id : ";

    private final Javers javers;
    private final AppUserRepository repository;
    private final LoginAttemptService loginAttemptService;


    public ResponseEntity<List<AppUser>> getAll() {
        try {
            List<AppUser> items = new ArrayList<>();

            repository.findAll().forEach(items::add);

            if (items.isEmpty()) {
                LOG.debug("La liste des appusers semble vide");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            LOG.info("Liste des appusers chargée");
            return new ResponseEntity<>(items, HttpStatus.OK);

        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant le chargement de la liste des appusers", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public AppUser getById(Long id) throws AppUserNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new AppUserNotFoundException(USER_NOT_FOUND + id));
    }


    public AppUser create(AppUser item) throws UsernameAlreadyExistException {
        AppUser savedItem = repository.save(item);
        LOG.info("Création d'un appuser");

        return savedItem;

    }

    public ResponseEntity<AppUser> update(Long id, AppUser item) throws AppUserNotFoundException {
        Optional<AppUser> existingItemOptional = repository.findById(id);

        if (existingItemOptional.isPresent()) {
            AppUser existingItem = existingItemOptional.get();
            Diff diff = javers.compare(existingItem, item);

            BeanUtils.copyProperties(item, existingItem);
            existingItem.setAppUserId(id);

            LOG.info("Mise à jour de l'appuser {}. MOdification des propriétées : {}", id, diff.getChanges());
            return new ResponseEntity<>(repository.save(existingItem), HttpStatus.OK);
        } else {
            LOG.debug("Impossible de trouver l'appuser avec l'id {}", id);
            throw  new AppUserNotFoundException(USER_NOT_FOUND + id);
        }
    }


    public ResponseEntity<HttpStatus> delete(Long id) {
        try {
            repository.deleteById(id);
            LOG.info("Suppression de l'appuser {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant la suppression de l'appuser", e);
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    public AppUser getByEmail(String email) {
        return repository.findAppUserByEmail(email).orElse(null);
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = repository.findAppUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format("Username with username %s not found", username)));
        try {
            validatingLoginAttempt(user);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return user;
    }

    private void validatingLoginAttempt(AppUser user) throws ExecutionException {
        if (user.isAccountNonLocked()) {
            if (loginAttemptService.hasExcedeedNumberOfAttempts(user.getUsername())) {
                user.setIsActive(false);
            } else {
                user.setIsActive(true);
            }

        } else {
            loginAttemptService.evictUserFromCache(user.getUsername());
        }
    }
}
