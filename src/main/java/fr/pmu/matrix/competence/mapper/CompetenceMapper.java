package fr.pmu.matrix.competence.mapper;

import fr.pmu.matrix.competence.domain.Competence;
import fr.pmu.matrix.competence.entity.CompetenceEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre les entités CompetenceEntity et les objets domaine Competence
 */
@Component
public class CompetenceMapper {

    /**
     * Convertit une entité compétence en objet domain
     * @param entity L'entité à convertir
     * @return L'objet domain correspondant
     */
    public Competence mapToCompetenceDomain(CompetenceEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Competence competence = new Competence();
        competence.setLibelle(entity.getLibelle());
        competence.setDescription(entity.getDescription());
        return competence;
    }
    
    /**
     * Convertit un objet domain en entité
     * @param domain L'objet domain à convertir
     * @return L'entité correspondante
     */
    public CompetenceEntity mapToCompetenceEntity(Competence domain) {
        if (domain == null) {
            return null;
        }
        
        CompetenceEntity entity = new CompetenceEntity();
        entity.setLibelle(domain.getLibelle());
        entity.setDescription(domain.getDescription());
        return entity;
    }
}