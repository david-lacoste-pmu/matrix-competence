package fr.pmu.matrix.competence.entity;

import fr.pmu.matrix.competence.domain.Nature;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entité JPA représentant une demande de recherche de compétences
 */
@Entity
@Table(name = "demande")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeEntity {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @Column(name = "matricule_demandeur", nullable = false)
    private String matriculeDemandeur;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "nature", nullable = false)
    private Nature nature;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "date_debut", nullable = false)
    private Date dateDebut;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "date_fin")
    private Date dateFin;
    
    @Column(name = "est_groupement", nullable = false)
    private Boolean estGroupement;
    
    @Column(name = "destination_code", nullable = false)
    private String destinationCode;
    
    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompetenceRequiseEntity> competencesRequises = new ArrayList<>();
}