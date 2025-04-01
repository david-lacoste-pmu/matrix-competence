package fr.pmu.matrix.competence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "personne")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonneEntity {
    @Id
    private String identifiant;
    
    private String nom;
    private String prenom;
    private String poste;
    
    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private EquipeEntity equipe;
}