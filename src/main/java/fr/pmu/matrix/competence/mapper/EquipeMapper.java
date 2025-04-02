package fr.pmu.matrix.competence.mapper;

import fr.pmu.matrix.competence.domain.Competence;
import fr.pmu.matrix.competence.domain.CompetenceRequise;
import fr.pmu.matrix.competence.domain.Equipe;
import fr.pmu.matrix.competence.domain.Groupement;
import fr.pmu.matrix.competence.domain.Note;
import fr.pmu.matrix.competence.domain.Personne;
import fr.pmu.matrix.competence.entity.CompetenceRequiseEntity;
import fr.pmu.matrix.competence.entity.EquipeEntity;
import fr.pmu.matrix.competence.entity.GroupementEntity;
import fr.pmu.matrix.competence.entity.PersonneEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour convertir entre les entités EquipeEntity et les objets domaine Equipe
 */
@Component
public class EquipeMapper {

    /**
     * Convertit une entité équipe en objet de domaine
     * 
     * @param entity Entité équipe
     * @param personneEntities Liste des personnes membres de l'équipe
     * @return Objet de domaine équipe
     */
    public Equipe convertToEquipe(EquipeEntity entity, List<PersonneEntity> personneEntities) {
        if (entity == null) {
            return null;
        }
        
        Equipe equipe = new Equipe();
        equipe.setCode(entity.getCode());
        equipe.setNom(entity.getNom());
        equipe.setDescription(entity.getDescription());
        
        // Convertir le groupement
        if (entity.getGroupement() != null) {
            GroupementEntity groupementEntity = entity.getGroupement();
            Groupement groupement = new Groupement();
            groupement.setCode(groupementEntity.getCode());
            groupement.setLibelle(groupementEntity.getLibelle());
            groupement.setDirection(groupementEntity.getDirection());
            equipe.setGroupement(groupement);
        }
        
        // Convertir les membres de l'équipe
        if (personneEntities != null && !personneEntities.isEmpty()) {
            List<Personne> membres = personneEntities.stream()
                    .map(this::convertToPersonneSansEquipe)
                    .collect(Collectors.toList());
            equipe.setMembres(membres);
        }
        
        // Convertir le profil de recherche
        if (entity.getProfilRecherche() != null && !entity.getProfilRecherche().isEmpty()) {
            List<CompetenceRequise> profilRecherche = entity.getProfilRecherche().stream()
                    .map(this::convertToCompetenceRequise)
                    .collect(Collectors.toList());
            equipe.setProfilRecherche(profilRecherche);
        }
        
        return equipe;
    }
    
    /**
     * Convertit une entité équipe en objet de domaine (sans charger les membres)
     * 
     * @param entity Entité équipe
     * @return Objet de domaine équipe
     */
    public Equipe convertToEquipeSansMembres(EquipeEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Equipe equipe = new Equipe();
        equipe.setCode(entity.getCode());
        equipe.setNom(entity.getNom());
        equipe.setDescription(entity.getDescription());
        
        // Convertir le groupement
        if (entity.getGroupement() != null) {
            GroupementEntity groupementEntity = entity.getGroupement();
            Groupement groupement = new Groupement();
            groupement.setCode(groupementEntity.getCode());
            groupement.setLibelle(groupementEntity.getLibelle());
            groupement.setDirection(groupementEntity.getDirection());
            equipe.setGroupement(groupement);
        }
        
        // Convertir le profil de recherche
        if (entity.getProfilRecherche() != null && !entity.getProfilRecherche().isEmpty()) {
            List<CompetenceRequise> profilRecherche = entity.getProfilRecherche().stream()
                    .map(this::convertToCompetenceRequise)
                    .collect(Collectors.toList());
            equipe.setProfilRecherche(profilRecherche);
        }
        
        return equipe;
    }

    /**
     * Convertit une entité personne en objet de domaine sans l'équipe pour éviter une référence circulaire
     * 
     * @param personneEntity Entité personne
     * @return Objet de domaine personne
     */
    private Personne convertToPersonneSansEquipe(PersonneEntity personneEntity) {
        if (personneEntity == null) {
            return null;
        }
        
        Personne personne = new Personne();
        personne.setIdentifiant(personneEntity.getIdentifiant());
        personne.setNom(personneEntity.getNom());
        personne.setPrenom(personneEntity.getPrenom());
        personne.setPoste(personneEntity.getPoste());
        return personne;
    }
    
    /**
     * Convertit une entité compétence requise en objet de domaine
     * 
     * @param entity Entité compétence requise
     * @return Objet de domaine compétence requise
     */
    private CompetenceRequise convertToCompetenceRequise(CompetenceRequiseEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Competence competence = new Competence();
        competence.setLibelle(entity.getCompetence().getLibelle());
        competence.setDescription(entity.getCompetence().getDescription());
        
        Note note = new Note();
        note.setValeur(entity.getNoteRequise().getValeur());
        note.setLibelle(entity.getNoteRequise().getLibelle());
        
        return new CompetenceRequise(competence, note);
    }
    
    /**
     * Convertit un objet domaine CompetenceRequise en entité
     * 
     * @param competenceRequise Objet domaine compétence requise
     * @return Entité compétence requise
     */
    public CompetenceRequiseEntity convertToCompetenceRequiseEntity(CompetenceRequise competenceRequise) {
        // Note: Cette méthode nécessite que les entités compétence et note existent déjà
        // Elle devrait être utilisée dans le cadre d'un service qui s'assure que ces entités existent
        return null; // À implémenter par le service responsable
    }
}