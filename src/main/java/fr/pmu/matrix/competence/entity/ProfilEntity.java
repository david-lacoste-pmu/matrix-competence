package fr.pmu.matrix.competence.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "profils")
@Getter
@Setter
public class ProfilEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personne_id", nullable = false)
    private PersonneEntity personne;
    
    @Column(name = "rapporteur", nullable = false)
    private String rapporteur;
    
    @Column(name = "date_debut_disponibilite")
    @Temporal(TemporalType.DATE)
    private Date dateDebutDisponibilite;
    
    @Column(name = "date_fin_disponibilite")
    @Temporal(TemporalType.DATE)
    private Date dateFinDisponibilite;
}