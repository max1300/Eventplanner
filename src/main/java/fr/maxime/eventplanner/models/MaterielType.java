package fr.maxime.eventplanner.models;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "materiel_type")
public class MaterielType {

    @SequenceGenerator(name = "materielType_sequence", sequenceName = "materielType_sequence", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "materielType_sequence")
    private Long materielTypeId;
    private String name;
    private String description;
}
