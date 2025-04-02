package fr.pmu.matrix.competence.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe représentant une demande de recherche de compétences
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Demande {
    
    /**
     * Identifiant unique de la demande
     */
    private String id;
    
    /**
     * Liste des compétences recherchées avec leur niveau requis
     */
    private List<CompetenceRequise> competencesRecherchees = new ArrayList<>();
    
    /**
     * Matricule de la personne ayant fait la demande
     */
    private String matriculeDemandeur;
    
    /**
     * Description détaillée de la demande
     */
    private String description;
    
    /**
     * Destination (Groupement ou Equipe) pour cette demande
     */
    private Destination destination;
    
    /**
     * Nature de la demande (PERMANENT, TEMPORAIRE, EXPERTISE)
     */
    private Nature nature;
    
    /**
     * Date de début souhaitée pour la disponibilité des compétences
     */
    private Date dateDebut;
    
    /**
     * Date de fin dans le cas d'une demande temporaire
     */
    private Date dateFin;
    
    /**
     * Ajoute une compétence requise à la liste des compétences recherchées
     * 
     * @param competence La compétence à ajouter
     * @param noteRequise Le niveau minimum requis pour cette compétence
     */
    public void ajouterCompetenceRequise(Competence competence, Note noteRequise) {
        CompetenceRequise competenceRequise = new CompetenceRequise(competence, noteRequise);
        competencesRecherchees.add(competenceRequise);
    }
    
    /**
     * Vérifie si la demande est valide
     * 
     * @return true si la demande est valide, false sinon
     */
    public boolean estValide() {
        // Pour une demande temporaire, la date de fin doit être définie et postérieure à la date de début
        if (nature == Nature.TEMPORAIRE) {
            if (dateFin == null || dateDebut == null || !dateFin.after(dateDebut)) {
                return false;
            }
        }
        
        // Une demande doit avoir au moins une compétence recherchée
        if (competencesRecherchees == null || competencesRecherchees.isEmpty()) {
            return false;
        }
        
        // Vérification des autres champs obligatoires
        return matriculeDemandeur != null && !matriculeDemandeur.isEmpty() 
                && destination != null 
                && nature != null 
                && dateDebut != null;
    }
}