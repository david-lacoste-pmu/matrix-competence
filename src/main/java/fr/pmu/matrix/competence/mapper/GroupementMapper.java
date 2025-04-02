package fr.pmu.matrix.competence.mapper;

import fr.pmu.matrix.competence.domain.Equipe;
import fr.pmu.matrix.competence.domain.Groupement;
import fr.pmu.matrix.competence.entity.EquipeEntity;
import fr.pmu.matrix.competence.entity.GroupementEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre les entités GroupementEntity et les objets domaine Groupement
 */
@Component
public class GroupementMapper {

    /**
     * Convertit une entité groupement en objet de domaine incluant les équipes
     * 
     * @param entity Entité groupement
     * @return Objet de domaine groupement
     */
    public Groupement mapToGroupementDomain(GroupementEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Groupement groupement = new Groupement();
        groupement.setCode(entity.getCode());
        groupement.setLibelle(entity.getLibelle());
        groupement.setDirection(entity.getDirection());

        return groupement;
    }
    
    /**
     * Convertit une entité groupement en objet de domaine sans les équipes
     * 
     * @param entity Entité groupement
     * @return Objet de domaine groupement
     */
    public Groupement mapToGroupementDomainSansEquipes(GroupementEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Groupement groupement = new Groupement();
        groupement.setCode(entity.getCode());
        groupement.setLibelle(entity.getLibelle());
        groupement.setDirection(entity.getDirection());
        return groupement;
    }
    
    /**
     * Convertit un objet de domaine en entité
     * 
     * @param domain Objet de domaine groupement
     * @return Entité groupement
     */
    public GroupementEntity mapToGroupementEntity(Groupement domain) {
        if (domain == null) {
            return null;
        }
        
        GroupementEntity entity = new GroupementEntity();
        entity.setCode(domain.getCode());
        entity.setLibelle(domain.getLibelle());
        entity.setDirection(domain.getDirection());
        return entity;
    }
    
    /**
     * Convertit une entité équipe en objet de domaine sans le groupement (pour éviter une référence circulaire)
     * 
     * @param equipeEntity Entité équipe
     * @return Objet de domaine équipe
     */
    private Equipe mapToEquipeSansGroupement(EquipeEntity equipeEntity) {
        if (equipeEntity == null) {
            return null;
        }
        
        Equipe equipe = new Equipe();
        equipe.setCode(equipeEntity.getCode());
        equipe.setNom(equipeEntity.getNom());
        equipe.setDescription(equipeEntity.getDescription());
        return equipe;
    }
}