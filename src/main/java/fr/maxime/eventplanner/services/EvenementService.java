package fr.maxime.eventplanner.services;

import fr.maxime.eventplanner.models.Evenement;
import fr.maxime.eventplanner.repositories.EvenementRepository;
import lombok.AllArgsConstructor;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EvenementService {

    public static final Logger LOG = LoggerFactory.getLogger(EvenementService.class);
    public static final String NOT_FOUND_MESSAGE = "Impossible de trouver l'evenement avec l'id %s";

    private final Javers javers;
    private final EvenementRepository repository;


    public ResponseEntity<List<Evenement>> getAll() {
        try {
            List<Evenement> items = new ArrayList<>();

            repository.findAll().forEach(items::add);

            if (items.isEmpty()) {
                LOG.debug("La liste des evenements semble vide");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            LOG.info("Liste des evenements chargée");
            return new ResponseEntity<>(items, HttpStatus.OK);

        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant le chargement de la liste des evenements", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Evenement> getById(Long id) throws NoResultException {
        Optional<Evenement> existingItemOptional = repository.findById(id);

        if (existingItemOptional.isPresent()) {
            LOG.info("evenement {} chargée", id);
            return new ResponseEntity<>(existingItemOptional.get(), HttpStatus.OK);
        } else {
            throw new NoResultException(String.format(NOT_FOUND_MESSAGE, id));
        }

    }


    @Transactional
    public ResponseEntity<Evenement> create(Evenement item) {
        try {
            Evenement savedItem = repository.save(item);
            LOG.info("Création d'un evenement");
            return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant la création de l'evenement", e);
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }


    @Transactional
    public ResponseEntity<Evenement> update(Long id, Evenement item) {
        Optional<Evenement> existingItemOptional = repository.findById(id);

        if (existingItemOptional.isPresent()) {
            Evenement existingItem = existingItemOptional.get();
            Diff diff = javers.compare(existingItem, item);

            BeanUtils.copyProperties(item, existingItem);
            existingItem.setEvenementId(id);

            LOG.info("Mise à jour de l'evenement {}. MOdification des propriétées : {}", id, diff.getChanges());
            return new ResponseEntity<>(repository.save(existingItem), HttpStatus.OK);
        } else {
            throw new NoResultException(String.format(NOT_FOUND_MESSAGE, id));
        }

    }


    public ResponseEntity<HttpStatus> delete(Long id) {
        Optional<Evenement> evenement = repository.findById(id);

        if (evenement.isPresent()) {
            repository.delete(evenement.get());
            LOG.info("Suppression de l'evenement {}", evenement.get().getEvenementId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new NoResultException(String.format(NOT_FOUND_MESSAGE, id));
        }
    }
}
