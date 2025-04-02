package fr.pmu.matrix.competence.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipes")
@Getter
@Setter
public class EquipeEntity {
    
    @Id
    @Column(name = "code", nullable = false)
    private String code;
    
    @Column(name = "nom", nullable = false)
    private String nom;
    
    @Column(name = "description")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupement_code")
    private GroupementEntity groupement;
    
    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonneEntity> membres = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
        name = "equipe_profil_recherche",
        joinColumns = @JoinColumn(name = "equipe_code"),
        inverseJoinColumns = @JoinColumn(name = "competence_requise_id")
    )
    private List<CompetenceRequiseEntity> profilRecherche = new ArrayList<>();
}