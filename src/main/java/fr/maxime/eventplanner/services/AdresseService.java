package fr.maxime.eventplanner.services;

import fr.maxime.eventplanner.models.Adresse;
import fr.maxime.eventplanner.repositories.AdresseRepository;
import lombok.AllArgsConstructor;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AdresseService {

    public static final Logger LOG = LoggerFactory.getLogger(AdresseService.class);

    private final Javers javers;
    private final AdresseRepository repository;


    public ResponseEntity<List<Adresse>> getAll() {
        try {
            List<Adresse> items = new ArrayList<>();

            repository.findAll().forEach(items::add);

            if (items.isEmpty()) {
                LOG.debug("La liste des adresses semble vide");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            LOG.info("Liste des adresses chargée");
            return new ResponseEntity<>(items, HttpStatus.OK);

        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant le chargement de la liste des adresses", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Adresse> getById(Long id) {
        Optional<Adresse> existingItemOptional = repository.findById(id);

        if (existingItemOptional.isPresent()) {
            LOG.info("adresse {} chargée", id);
            return new ResponseEntity<>(existingItemOptional.get(), HttpStatus.OK);

        } else {
            LOG.debug("Impossible de trouver l'adresse avec l'id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @Transactional
    public ResponseEntity<Adresse> create(Adresse item) {
        try {
            Adresse savedItem = repository.save(item);
            LOG.info("Création d'un adresse");
            return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant la création de l'adresse", e);
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }


    @Transactional
    public ResponseEntity<Adresse> update(Long id, Adresse item) {
        Optional<Adresse> existingItemOptional = repository.findById(id);

        if (existingItemOptional.isPresent()) {
            Adresse existingItem = existingItemOptional.get();
            Diff diff = javers.compare(existingItem, item);

            BeanUtils.copyProperties(item, existingItem);
            existingItem.setId(id);

            LOG.info("Mise à jour de l'adresse {}. MOdification des propriétées : {}", id, diff.getChanges());
            return new ResponseEntity<>(repository.save(existingItem), HttpStatus.OK);
        } else {
            LOG.debug("Impossible de trouver l'adresse avec l'id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<HttpStatus> delete(Long id) {
        try {
            repository.deleteById(id);
            LOG.info("Suppression de l'adresse {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant la suppression de l'adresse", e);
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }
}
