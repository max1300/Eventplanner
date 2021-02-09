package fr.maxime.eventplanner.repositories;

import fr.maxime.eventplanner.models.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {
}