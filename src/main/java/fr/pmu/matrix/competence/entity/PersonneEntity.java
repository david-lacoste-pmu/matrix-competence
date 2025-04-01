package fr.pmu.matrix.competence.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "personnes")
@Getter
@Setter
public class PersonneEntity {
    
    @Id
    @Column(name = "identifiant", nullable = false)
    private String identifiant;
    
    @Column(name = "nom", nullable = false)
    private String nom;
    
    @Column(name = "prenom", nullable = false)
    private String prenom;
    
    @Column(name = "poste")
    private String poste;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_code")
    private EquipeEntity equipe;
}