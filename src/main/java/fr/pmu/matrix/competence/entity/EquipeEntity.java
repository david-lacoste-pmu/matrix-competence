package fr.pmu.matrix.competence.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "equipe")
@Data
@NoArgsConstructor
public class EquipeEntity {
    @Id
    private String code;
    
    private String nom;
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "groupement_id")
    private GroupementEntity groupement;
    
    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonneEntity> membres = new ArrayList<>();
    
    public EquipeEntity(String nom, String description, String code, GroupementEntity groupement) {
        this.nom = nom;
        this.description = description;
        this.code = code;
        this.groupement = groupement;
    }
}