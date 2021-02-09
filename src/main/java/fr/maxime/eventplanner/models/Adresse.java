package fr.maxime.eventplanner.models;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "adresse")
public class Adresse {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "adresse_sequence")
    @SequenceGenerator(name = "adresse_sequence", sequenceName = "adresse_sequence", allocationSize = 1)
    @Column(name = "ADRESSE_ID", nullable = false)
    private Long id;

    private String ville;
    private Integer adresseNumber;
    private String adresse;
    private Integer codePostal;

}
