package fr.pmu.matrix.competence.domain;

import java.util.List;
import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Equipe {
    private String nom;
    private String description;
    private String code;
    private Groupement groupement;
    private List<Personne> membres = new ArrayList<>();
    
    public Equipe(String nom, String description, String code, Groupement groupement) {
        this.nom = nom;
        this.description = description;
        this.code = code;
        this.groupement = groupement;
    }
    
    public void ajouterMembre(Personne personne) {
        if (personne != null && !membres.contains(personne)) {
            membres.add(personne);
            personne.setEquipe(this);
        }
    }
    
    public void retirerMembre(Personne personne) {
        if (personne != null && membres.contains(personne)) {
            membres.remove(personne);
            if (this.equals(personne.getEquipe())) {
                personne.setEquipe(null);
            }
        }
    }
}