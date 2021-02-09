package fr.maxime.eventplanner.services;

import fr.maxime.eventplanner.models.Participant;
import fr.maxime.eventplanner.repositories.ParticipantRepository;
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
public class ParticipantService {

    public static final Logger LOG = LoggerFactory.getLogger(ParticipantService.class);

    private final Javers javers;
    private final ParticipantRepository repository;


    public ResponseEntity<List<Participant>> getAll() {
        try {
            List<Participant> items = new ArrayList<>();

            repository.findAll().forEach(items::add);

            if (items.isEmpty()) {
                LOG.debug("La liste des participants semble vide");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            LOG.info("Liste des participants chargée");
            return new ResponseEntity<>(items, HttpStatus.OK);

        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant le chargement de la liste des participants", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Participant> getById(Long id) {
        Optional<Participant> existingItemOptional = repository.findById(id);

        if (existingItemOptional.isPresent()) {
            LOG.info("participant {} chargée", id);
            return new ResponseEntity<>(existingItemOptional.get(), HttpStatus.OK);

        } else {
            LOG.debug("Impossible de trouver l'participant avec l'id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @Transactional
    public ResponseEntity<Participant> create(Participant item) {
        try {
            Participant savedItem = repository.save(item);
            LOG.info("Création d'un participant");
            return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant la création de l'participant", e);
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }


    @Transactional
    public ResponseEntity<Participant> update(Long id, Participant item) {
        Optional<Participant> existingItemOptional = repository.findById(id);

        if (existingItemOptional.isPresent()) {
            Participant existingItem = existingItemOptional.get();
            Diff diff = javers.compare(existingItem, item);

            BeanUtils.copyProperties(item, existingItem);
            existingItem.setParticipantId(id);

            LOG.info("Mise à jour de l'participant {}. MOdification des propriétées : {}", id, diff.getChanges());
            return new ResponseEntity<>(repository.save(existingItem), HttpStatus.OK);
        } else {
            LOG.debug("Impossible de trouver l'participant avec l'id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<HttpStatus> delete(Long id) {
        try {
            repository.deleteById(id);
            LOG.info("Suppression de l'participant {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            LOG.error("Une erreur est survenue pendant la suppression de l'participant", e);
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }


}
