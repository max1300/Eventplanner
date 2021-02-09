package fr.maxime.eventplanner.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "evenement")
public class Evenement {

    @SequenceGenerator(name = "evenement_sequence", sequenceName = "evenement_sequence", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "evenement_sequence")
    private Long evenementId;
    private String title;
    private LocalDate start;
    private LocalDate end;
    private LocalDate created_at;
    private LocalDate updated_at;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "ORGANISEUR_APPUSERID")
    private AppUser organiseur;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<Participant> participants = new HashSet<>();

    @OneToOne(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "ADRESSE_ADRESSEID")
    private Adresse adresse;

    public Evenement addParticipant(Participant participant) {
        participant.setEvenement(this);
        this.participants.add(participant);
        return this;
    }


}
