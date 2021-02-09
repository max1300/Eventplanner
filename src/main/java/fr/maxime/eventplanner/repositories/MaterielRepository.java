package fr.maxime.eventplanner.repositories;

import fr.maxime.eventplanner.models.Materiel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterielRepository extends JpaRepository<Materiel, Long> {
}