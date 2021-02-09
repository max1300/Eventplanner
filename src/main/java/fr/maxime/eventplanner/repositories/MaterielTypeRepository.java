package fr.maxime.eventplanner.repositories;

import fr.maxime.eventplanner.models.MaterielType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterielTypeRepository extends JpaRepository<MaterielType, Long> {
}