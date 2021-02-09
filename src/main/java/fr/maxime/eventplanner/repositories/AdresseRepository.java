package fr.maxime.eventplanner.repositories;

import fr.maxime.eventplanner.models.Adresse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdresseRepository extends JpaRepository<Adresse, Long> {
}