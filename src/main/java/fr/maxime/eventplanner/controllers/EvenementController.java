package fr.maxime.eventplanner.controllers;

import fr.maxime.eventplanner.exceptions.ExceptionsHandler;
import fr.maxime.eventplanner.models.Evenement;
import fr.maxime.eventplanner.services.EvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evenements")
public class EvenementController extends ExceptionsHandler {

    @Autowired
    EvenementService service;

    @GetMapping
    public ResponseEntity<List<Evenement>> getAllEvenements() {
        return service.getAll();
    }

    @PostMapping
    public ResponseEntity<Evenement> createOne(@RequestBody Evenement evenement) {
        return service.create(evenement);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evenement> getOneById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evenement> updateOne(@PathVariable("id") Long id, @RequestBody Evenement evenement) {
        return service.update(id, evenement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteOne(@PathVariable("id") Long id) {
        return service.delete(id);
    }
}
