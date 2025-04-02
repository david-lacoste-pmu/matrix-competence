package fr.pmu.matrix.competence.mapper;

import fr.pmu.matrix.competence.domain.Equipe;
import fr.pmu.matrix.competence.domain.Personne;
import fr.pmu.matrix.competence.entity.EquipeEntity;
import fr.pmu.matrix.competence.entity.PersonneEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre les entités PersonneEntity et les objets domaine Personne
 */
@Component
public class PersonneMapper {

    /**
     * Convertit une entité personne en objet de domaine incluant l'équipe
     * 
     * @param entity Entité personne
     * @return Objet de domaine personne
     */
    public Personne mapToPersonneDomain(PersonneEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Personne personne = new Personne();
        personne.setIdentifiant(entity.getIdentifiant());
        personne.setNom(entity.getNom());
        personne.setPrenom(entity.getPrenom());
        personne.setPoste(entity.getPoste());
        
        // Convertir l'équipe
        if (entity.getEquipe() != null) {
            EquipeEntity equipeEntity = entity.getEquipe();
            Equipe equipe = new Equipe();
            equipe.setCode(equipeEntity.getCode());
            equipe.setNom(equipeEntity.getNom());
            equipe.setDescription(equipeEntity.getDescription());
            personne.setEquipe(equipe);
        }
        
        return personne;
    }
    
    /**
     * Convertit une entité personne en objet de domaine sans l'équipe
     * 
     * @param entity Entité personne
     * @return Objet de domaine personne
     */
    public Personne mapToPersonneDomainSansEquipe(PersonneEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Personne personne = new Personne();
        personne.setIdentifiant(entity.getIdentifiant());
        personne.setNom(entity.getNom());
        personne.setPrenom(entity.getPrenom());
        personne.setPoste(entity.getPoste());
        return personne;
    }
    
    /**
     * Convertit un objet de domaine en entité
     * 
     * @param domain Objet de domaine personne
     * @return Entité personne
     */
    public PersonneEntity mapToPersonneEntity(Personne domain) {
        if (domain == null) {
            return null;
        }
        
        PersonneEntity entity = new PersonneEntity();
        entity.setIdentifiant(domain.getIdentifiant());
        entity.setNom(domain.getNom());
        entity.setPrenom(domain.getPrenom());
        entity.setPoste(domain.getPoste());
        // Note: L'équipe doit être définie séparément pour éviter les références circulaires
        return entity;
    }
}