package fr.maxime.eventplanner.services;

import fr.maxime.eventplanner.models.Materiel;
import fr.maxime.eventplanner.repositories.MaterielRepository;
import lombok.AllArgsConstructor;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MaterielService {

    public static final Logger LOG = LoggerFactory.getLogger(MaterielService.class);

    private final Javers javers;
    private final MaterielRepository repository;


    public ResponseEntity<List<Materiel>> getAll() {
        try {
            List<Materiel> items = new ArrayList<>();

            repository.findAll().forEach(items::add);

            if (items.isEmpty()) {
                LOG.debug("La liste des materiels semble vide");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            LOG.info("Liste des materiels chargée");
            return new ResponseEntity<>(items, HttpStatus.OK);

        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant le chargement de la liste des materiels", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Materiel> getById(Long id) {
        Optional<Materiel> existingItemOptional = repository.findById(id);

        if (existingItemOptional.isPresent()) {
            LOG.info("materiel {} chargée", id);
            return new ResponseEntity<>(existingItemOptional.get(), HttpStatus.OK);

        } else {
            LOG.debug("Impossible de trouver l'materiel avec l'id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @Transactional
    public ResponseEntity<Materiel> create(Materiel item) {
        try {
            Materiel savedItem = repository.save(item);
            LOG.info("Création d'un materiel");
            return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant la création de l'materiel", e);
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }


    @Transactional
    public ResponseEntity<Materiel> update(Long id, Materiel item) {
        Optional<Materiel> existingItemOptional = repository.findById(id);

        if (existingItemOptional.isPresent()) {
            Materiel existingItem = existingItemOptional.get();
            Diff diff = javers.compare(existingItem, item);

            BeanUtils.copyProperties(item, existingItem);
            existingItem.setMaterielId(id);

            LOG.info("Mise à jour de l'materiel {}. MOdification des propriétées : {}", id, diff.getChanges());
            return new ResponseEntity<>(repository.save(existingItem), HttpStatus.OK);
        } else {
            LOG.debug("Impossible de trouver l'materiel avec l'id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<HttpStatus> delete(Long id) {
        try {
            repository.deleteById(id);
            LOG.info("Suppression de l'materiel {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant la suppression de l'materiel", e);
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }
}
