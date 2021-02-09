package fr.maxime.eventplanner.models;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "participant")
public class Participant {

    @SequenceGenerator(name = "participant_sequence", sequenceName = "participant_sequence", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "participant_sequence")
    private Long participantId;
    private String firstName;
    private String LastName;

    @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<Materiel> materiels = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "EVENEMENT_EVENEMENTID")
    private Evenement evenement;

    public Participant addMateriel(Materiel materiel) {
        materiel.setProprietaire(this);
        this.materiels.add(materiel);
        return this;
    }

}
