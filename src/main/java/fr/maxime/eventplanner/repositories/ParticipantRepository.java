package fr.maxime.eventplanner.repositories;

import fr.maxime.eventplanner.models.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}