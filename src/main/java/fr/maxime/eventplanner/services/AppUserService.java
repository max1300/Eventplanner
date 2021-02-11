package fr.maxime.eventplanner.services;

import fr.maxime.eventplanner.mail.MailSender;
import fr.maxime.eventplanner.models.AppUser;
import fr.maxime.eventplanner.models.ConfirmationToken;
import fr.maxime.eventplanner.repositories.AppUserRepository;
import lombok.AllArgsConstructor;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AppUserService {

    public static final Logger LOG = LoggerFactory.getLogger(AppUserService.class);
    public static final String USERNAME_ALREADY_TAKEN = "Ce username est déjà pris";

    private final Javers javers;
    private final AppUserRepository repository;
    private final ConfirmationTokenService confirmationTokenService;
    private final MailSender mailSender;
    private final BCryptPasswordEncoder bCryptEncoder;


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

    public AppUser getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id : " + id));
    }


    @Transactional
    public ResponseEntity<AppUser> create(AppUser item) {
        AppUser user = repository.findAppUserByUsername(item.getUsername())
                .orElse(null);
        if (user != null) {
            throw new IllegalStateException(USERNAME_ALREADY_TAKEN);
        }

        try {
            String encodedPass = bCryptEncoder.encode(item.getPassword());
            item.setPassword(encodedPass);

            AppUser savedItem = repository.save(item);
            LOG.info("Création d'un appuser");

            ConfirmationToken confirmationToken = confirmationTokenService.createConfirmationToken(savedItem);
            ConfirmationToken save = confirmationTokenService.save(confirmationToken);

            String link = "http://localhost:8080/authentication/confirm?token=" + save.getToken();
            mailSender.send(savedItem.getUsername(), mailSender.buildEmail(savedItem.getUsername(), link));
            return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant la création de l'appuser", e);
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Transactional
    public ResponseEntity<AppUser> update(Long id, AppUser item) {
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
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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


}
