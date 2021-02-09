package fr.maxime.eventplanner.models;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "materiel")
public class Materiel {

    @SequenceGenerator(name = "materiel_sequence", sequenceName = "materiel_sequence", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "materiel_sequence")
    private Long materielId;

    @OneToOne
    private MaterielType type;

    @ManyToOne
    @JoinColumn(name = "PROPRIETAIRE_PARTICIPANTID")
    private Participant proprietaire;

}
