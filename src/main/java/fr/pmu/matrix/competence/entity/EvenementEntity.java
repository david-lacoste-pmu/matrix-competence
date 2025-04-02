package fr.pmu.matrix.competence.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "evenements")
@Getter
@Setter
public class EvenementEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "date_debut", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDebut;
    
    @Column(name = "date_fin", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFin;
    
    @Column(name = "type", nullable = false)
    private String type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "criticite", nullable = false)
    private Criticite criticite;
    
    // Relations avec les participants
    @ManyToMany
    @JoinTable(
        name = "evenement_personne",
        joinColumns = @JoinColumn(name = "evenement_id"),
        inverseJoinColumns = @JoinColumn(name = "personne_id")
    )
    private List<PersonneEntity> personneParticipants;
    
    @ManyToMany
    @JoinTable(
        name = "evenement_equipe",
        joinColumns = @JoinColumn(name = "evenement_id"),
        inverseJoinColumns = @JoinColumn(name = "equipe_code")
    )
    private List<EquipeEntity> equipeParticipants;
    
    public enum Criticite {
        MINOR, CRITICAL, MAJOR
    }
}