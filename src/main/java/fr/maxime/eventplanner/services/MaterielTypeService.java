package fr.maxime.eventplanner.services;

import fr.maxime.eventplanner.models.MaterielType;
import fr.maxime.eventplanner.repositories.MaterielTypeRepository;
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
public class MaterielTypeService {

    public static final Logger LOG = LoggerFactory.getLogger(MaterielTypeService.class);

    private final Javers javers;
    private final MaterielTypeRepository repository;


    public ResponseEntity<List<MaterielType>> getAll() {
        try {
            List<MaterielType> items = new ArrayList<>();

            repository.findAll().forEach(items::add);

            if (items.isEmpty()) {
                LOG.debug("La liste des materielType semble vide");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            LOG.info("Liste des materielType chargée");
            return new ResponseEntity<>(items, HttpStatus.OK);

        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant le chargement de la liste des materielType", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<MaterielType> getById(Long id) {
        Optional<MaterielType> existingItemOptional = repository.findById(id);

        if (existingItemOptional.isPresent()) {
            LOG.info("materielType {} chargée", id);
            return new ResponseEntity<>(existingItemOptional.get(), HttpStatus.OK);

        } else {
            LOG.debug("Impossible de trouver l'materielType avec l'id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @Transactional
    public ResponseEntity<MaterielType> create(MaterielType item) {
        try {
            MaterielType savedItem = repository.save(item);
            LOG.info("Création d'un materielType");
            return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant la création du materielType", e);
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }


    @Transactional
    public ResponseEntity<MaterielType> update(Long id, MaterielType item) {
        Optional<MaterielType> existingItemOptional = repository.findById(id);

        if (existingItemOptional.isPresent()) {
            MaterielType existingItem = existingItemOptional.get();
            Diff diff = javers.compare(existingItem, item);

            BeanUtils.copyProperties(item, existingItem);
            existingItem.setMaterielTypeId(id);

            LOG.info("Mise à jour dumaterielType {}. Modification des propriétées : {}", id, diff.getChanges());
            return new ResponseEntity<>(repository.save(existingItem), HttpStatus.OK);
        } else {
            LOG.debug("Impossible de trouver le materielType avec l'id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<HttpStatus> delete(Long id) {
        try {
            repository.deleteById(id);
            LOG.info("Suppression de le materielType {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant la suppression de le materielType", e);
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }
}
