package fr.pmu.matrix.competence.mapper;

import fr.pmu.matrix.competence.domain.Equipe;
import fr.pmu.matrix.competence.domain.Groupement;
import fr.pmu.matrix.competence.domain.Personne;
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
}